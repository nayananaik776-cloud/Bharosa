"""
Bharosa Fraud Intelligence Layer (ml package)
Exported entry points for backend and test suite integration.
"""

from ml.interface import evaluate_payment_risk
from ml.intent_analyzer import analyze_intent, IntentAnalyzer
from ml.promise_analyzer import analyze_promise, PromiseAnalyzer
from ml.escalation_detector import detect_escalation, EscalationDetector
from ml.scam_signals import extract_scam_signals, ScamSignalExtractor
from ml.transaction_features import extract_transaction_features, TransactionFeatureExtractor
from ml.risk_model import calculate_ml_risk, BharosaRiskModel

__all__ = [
    "evaluate_payment_risk",
    "analyze_intent",
    "IntentAnalyzer",
    "analyze_promise",
    "PromiseAnalyzer",
    "detect_escalation",
    "EscalationDetector",
    "extract_scam_signals",
    "ScamSignalExtractor",
    "extract_transaction_features",
    "TransactionFeatureExtractor",
    "calculate_ml_risk",
    "BharosaRiskModel"
]
