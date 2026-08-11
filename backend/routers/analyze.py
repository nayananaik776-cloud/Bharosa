from fastapi import APIRouter
from ..schemas import AnalyzeFullRequest, AnalyzeFullResponse, AnalyzeMessageRequest, AnalyzeTransactionRequest
from ..services import risk_engine

router = APIRouter(prefix="/api/analyze", tags=["analyze"])

@router.post("/full", response_model=AnalyzeFullResponse)
def analyze_full(request: AnalyzeFullRequest):
    # Pass to orchestration engine
    result = risk_engine.evaluate_risk(request)
    return result

@router.post("/message")
def analyze_message(request: AnalyzeMessageRequest):
    return {"status": "not_implemented"}

@router.post("/transaction")
def analyze_transaction(request: AnalyzeTransactionRequest):
    return {"status": "not_implemented"}
