from fastapi import APIRouter
from ..schemas import RecipientCheckRequest

router = APIRouter(prefix="/api/recipient", tags=["recipient"])

@router.post("/check")
def check_recipient(request: RecipientCheckRequest):
    return {"recipient_id": request.recipient_id, "risk_score": 0, "reports": 0}
