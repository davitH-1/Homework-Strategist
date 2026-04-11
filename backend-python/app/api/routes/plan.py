from fastapi import APIRouter, HTTPException
from app.schemas.plan import PlanRequest, PlanResponse
from app.services.plan_service import PlanService
from app.models.plan import ClassItem

router = APIRouter(prefix="/plan", tags=["plan"])
service = PlanService()


@router.post("", response_model=PlanResponse)
async def create_plan(request: PlanRequest) -> PlanResponse:
    classes = [
        ClassItem(
            name=c.name,
            due_date=c.dueDate,
            estimated_hours=c.estimatedHours,
        )
        for c in request.classes
    ]

    try:
        plan, ai_summary, scheduled_events = service.create_plan(request.userId, classes)
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

    return PlanResponse(
        userId=plan.user_id,
        message=ai_summary,
        scheduledEvents=scheduled_events,
    )