from dataclasses import dataclass, field
from typing import List


@dataclass
class ClassItem:
    name: str
    due_date: str
    estimated_hours: float


@dataclass
class Plan:
    user_id: str
    classes: List[ClassItem] = field(default_factory=list)