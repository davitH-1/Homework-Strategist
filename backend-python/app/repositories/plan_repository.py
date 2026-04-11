from typing import List

from app.models.plan import Plan, ClassItem


class PlanRepository:
    def create(self, user_id: str, classes: List[ClassItem]) -> Plan:
        # TODO: persist to database
        return Plan(user_id=user_id, classes=classes)