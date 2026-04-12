import datetime

from fastapi import APIRouter, HTTPException
from googleapiclient.discovery import build
from pydantic import BaseModel

from app.api.routes.auth import get_calendar_credentials

router = APIRouter(prefix="/api/calendar", tags=["calendar"])


class CalendarEventRequest(BaseModel):
    title: str
    date: str          # ISO date string, e.g. "2026-04-15"
    time: str | None = None    # "HH:mm", optional
    description: str | None = None


@router.post("/events")
async def create_calendar_event(request: CalendarEventRequest):
    creds = get_calendar_credentials()

    try:
        service = build("calendar", "v3", credentials=creds)
    except Exception as exc:
        raise HTTPException(status_code=500, detail=f"Could not build Calendar service: {exc}") from exc

    if request.time:
        start_str = f"{request.date}T{request.time}:00"
        start_dt = datetime.datetime.fromisoformat(start_str)
        end_dt = start_dt + datetime.timedelta(hours=1)
        start_body = {"dateTime": start_dt.isoformat(), "timeZone": "America/Los_Angeles"}
        end_body = {"dateTime": end_dt.isoformat(), "timeZone": "America/Los_Angeles"}
    else:
        # All-day event
        start_body = {"date": request.date}
        end_body = {"date": request.date}

    event = {
        "summary": request.title,
        "description": request.description or "",
        "start": start_body,
        "end": end_body,
    }

    try:
        created = service.events().insert(calendarId="primary", body=event).execute()
    except Exception as exc:
        raise HTTPException(status_code=500, detail=f"Calendar API error: {exc}") from exc

    return {"status": "created", "eventId": created.get("id")}