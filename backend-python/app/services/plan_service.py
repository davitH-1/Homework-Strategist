import datetime
from typing import List

from googleapiclient.discovery import build

from app.gemma.ai_model import GemmaModel
from app.models.plan import Plan, ClassItem
from app.repositories.plan_repository import PlanRepository
from app.services.findfreeslots import authenticate, fetch_events, day_window_boundaries, next_10am_local
from app.services.createevents import create_event, find_first_available_week_slot


class PlanService:
    def __init__(self, repository: PlanRepository = PlanRepository()):
        self.repository = repository
        self.gemma = GemmaModel()

    def create_plan(self, user_id: str, classes: List[ClassItem]) -> tuple[Plan, list[str]]:
        plan = self.repository.create(user_id, classes)
        ai_summary = self._generate_ai_summary(classes)
        scheduled = self._schedule_on_calendar(classes)
        return plan, ai_summary, scheduled

    def _generate_ai_summary(self, classes: List[ClassItem]) -> str:
        class_lines = "\n".join(
            f"- {c.name}: due {c.due_date}, estimated {c.estimated_hours}h"
            for c in classes
        )
        prompt = (
            f"You are a study planner assistant. Given these upcoming assignments, "
            f"provide a brief prioritized study plan:\n{class_lines}"
        )
        return self.gemma.generate(prompt)

    def _schedule_on_calendar(self, classes: List[ClassItem]) -> list[str]:
        creds = authenticate()
        service = build("calendar", "v3", credentials=creds)

        now = datetime.datetime.now(datetime.timezone.utc).astimezone()
        window_start = max(next_10am_local(), now)
        week_windows = day_window_boundaries(window_start)
        if not week_windows:
            return []

        window_end = week_windows[-1][1]
        events = fetch_events(service, window_start, window_end)

        scheduled_ids = []
        for cls in classes:
            duration_minutes = int(cls.estimated_hours * 60)
            slot = find_first_available_week_slot(events, window_start, duration_minutes)
            if not slot:
                continue

            start_time, end_time = slot
            actual_end = start_time + datetime.timedelta(minutes=duration_minutes)
            created = create_event(
                service,
                summary=f"Study: {cls.name}",
                description=f"Due: {cls.due_date}",
                start_time=start_time,
                end_time=actual_end,
            )
            scheduled_ids.append(created["id"])

            # Add the new event to busy list so next class doesn't overlap
            events.append({
                "start": {"dateTime": start_time.isoformat()},
                "end": {"dateTime": actual_end.isoformat()},
            })

        return scheduled_ids