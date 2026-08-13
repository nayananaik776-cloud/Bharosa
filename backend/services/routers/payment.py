from fastapi import APIRouter
from ..schemas import PaymentSimulateRequest

router = APIRouter(prefix="/api/payment", tags=["payment"])

@router.post("/simulate")
def simulate_payment(request: PaymentSimulateRequest):
    return {"status": "success", "transaction_id": request.transaction_id, "action_taken": request.action}
