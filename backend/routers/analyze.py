from fastapi import APIRouter, Request
from typing import Dict, Any
from ..schemas import AnalyzeFullRequest, AnalyzeFullResponse, AnalyzeMessageRequest, AnalyzeTransactionRequest
from ..services import risk_engine

router = APIRouter(tags=["analyze"])

@router.post("/api/analyze/full", response_model=AnalyzeFullResponse)
def analyze_full(request: AnalyzeFullRequest):
    return risk_engine.evaluate_risk(request)

@router.post("/api/v1/analyze")
def analyze_v1(payload: Dict[str, Any]):
    text = payload.get("text", payload.get("message_text", payload.get("message", "")))
    txn_data = payload.get("transaction", {"amount": payload.get("amount", 0.0), "direction": "SEND", "recipient_id": "unknown", "recipient_name": "unknown", "is_new_recipient": True})
    
    req = AnalyzeFullRequest(
        message_text=text,
        transaction=txn_data,
        user_context=payload.get("user_context")
    )
    res = risk_engine.evaluate_risk(req)
    
    # Return structure compatible with Android mock v1 schema
    return {
        "risk_score": res.risk_score / 100.0,
        "risk_level": res.risk_level,
        "category": res.signals[0] if res.signals and isinstance(res.signals[0], str) else "FINANCIAL_RISK",
        "is_scam": res.risk_level in ["HIGH", "CRITICAL"],
        "warning_title": {
            "en": res.recommended_action or "BE CAREFUL",
            "hi": "सावधान रहें"
        },
        "warning_message": {
            "en": res.explanation,
            "hi": res.explanation
        },
        "action_required": res.decision,
        "explanation_audio_text": {
            "en": res.voice_text or res.explanation,
            "hi": res.voice_text or res.explanation
        }
    }

@router.post("/api/analyze/message")
def analyze_message(request: AnalyzeMessageRequest):
    return {"status": "implemented", "message": request.message_text}

@router.post("/api/analyze/transaction")
def analyze_transaction(request: AnalyzeTransactionRequest):
    return {"status": "implemented", "transaction": request.transaction}

