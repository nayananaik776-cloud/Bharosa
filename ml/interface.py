"""
Bharosa Fraud Intelligence - Backend Interface Layer
Provides standardized python interface for Member 1 (Backend API) to consume.
No HTTP server needed; direct module import.
"""

from typing import Dict, Any, List, Optional
from ml.risk_model import calculate_ml_risk, BharosaRiskModel
from ml.intent_analyzer import analyze_intent
from ml.promise_analyzer import analyze_promise
from ml.escalation_detector import detect_escalation
from ml.scam_signals import extract_scam_signals
from ml.transaction_features import extract_transaction_features

_GLOBAL_RISK_MODEL = BharosaRiskModel()


def evaluate_payment_risk(
    message: Optional[str],
    transaction: Dict[str, Any],
    history: Optional[List[Dict[str, Any]]] = None
) -> Dict[str, Any]:
    """
    Primary API Function for Backend Integration.
    Evaluates payment context and returns full risk payload.

    Example Usage for Member 1 (Backend):
    --------------------------------------
    from ml import evaluate_payment_risk

    result = evaluate_payment_risk(
        message="Scan QR to receive ₹5,000 refund",
        transaction={"amount": 5000, "direction": "SEND", "recipient": "scammer@upi"},
        history=[]
    )

    print(result["decision"]) # PAUSE
    print(result["primary_warning"]) # STOP — You are about to SEND ₹5,000, not RECEIVE ₹5,000.
    """
    return _GLOBAL_RISK_MODEL.calculate_risk(message, transaction, history)
