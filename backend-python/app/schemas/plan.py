from pydantic import BaseModel
from typing import List


class ClassItem(BaseModel):
    name: str
    dueDate: str        # ISO format: YYYY-MM-DD
    estimatedHours: float


class PlanRequest(BaseModel):
    userId: str
    classes: List[ClassItem]


class PlanResponse(BaseModel):
    userId: str
    message: str
    scheduledEvents: List[str]