"""
Bharosa Fraud Intelligence - Hybrid Risk Scoring Model & Risk Orchestrator
Combines Rules + NLP + ML + Contextual Risk Scoring into explainable risk decisions
(LOW / MEDIUM / HIGH / CRITICAL) and actionable payment recommendations (ALLOW / VERIFY / PAUSE).
"""

from typing import Dict, Any, List, Optional
from ml.intent_analyzer import analyze_intent, ACTION_UNKNOWN
from ml.promise_analyzer import analyze_promise
from ml.escalation_detector import detect_escalation
from ml.scam_signals import extract_scam_signals, ScamSignalExtractor
from ml.transaction_features import extract_transaction_features


class BharosaRiskModel:
    """Hybrid Risk Scoring Model combining rules, NLP, ML features, and contextual signals."""

    def __init__(self):
        self.signal_extractor = ScamSignalExtractor()

    def calculate_risk(
        self,
        message: Optional[str],
        transaction: Dict[str, Any],
        history: Optional[List[Dict[str, Any]]] = None
    ) -> Dict[str, Any]:
        """
        Main entry point for evaluating payment risk.
        Returns unified risk decision payload.
        """
        history = history or []
        
        # 1. Component Intelligence Analysis
        intent_res = analyze_intent(message, transaction)
        promise_res = analyze_promise(message)
        escalation_res = detect_escalation(history)
        scam_signals = extract_scam_signals(message, transaction)
        txn_features = extract_transaction_features(transaction, history)

        # Merge payment_before_benefit into transaction context if promise analyzer found it
        if promise_res.get("payment_before_benefit"):
            transaction["payment_before_benefit"] = True
            if "payment_before_benefit" not in scam_signals:
                scam_signals.append("payment_before_benefit")

        # 2. Contextual Risk Score Calculation (0.0 to 1.0)
        signal_score = self.signal_extractor.calculate_signal_score(scam_signals) / 10.0  # max 1.0
        
        # Intent Mismatch Weight
        intent_weight = 0.50 if intent_res.get("mismatch") else 0.0
        
        # Promise Weight
        promise_weight = 0.35 if promise_res.get("payment_before_benefit") else 0.0

        # Escalation Weight
        escalation_level = escalation_res.get("escalation_level", "NONE")
        escalation_weight_map = {"NONE": 0.0, "LOW": 0.1, "MODERATE": 0.3, "HIGH": 0.6, "CRITICAL": 0.85}
        escalation_weight = escalation_weight_map.get(escalation_level, 0.0)

        # Behavioral & Transaction Weight
        txn_weight = 0.0
        if txn_features.get("is_new_recipient"):
            txn_weight += 0.10
        if txn_features.get("is_unusual_amount"):
            txn_weight += 0.15
        if txn_features.get("is_unusual_time"):
            txn_weight += 0.10

        # Base Combined Score
        raw_score = (signal_score * 0.25) + intent_weight + promise_weight + escalation_weight + (txn_weight * 0.15)
        risk_score = round(min(max(raw_score, 0.05), 0.99), 2)

        # 3. Rule Override & Decision Logic
        risk_level = "LOW"
        decision = "ALLOW"
        primary_warning = None
        detailed_explanation = "This transaction appears safe and normal."
        voice_summary = "Payment looks safe. You can proceed."

        # Rule 1: CRITICAL INTENT MISMATCH (Receive vs Send)
        if intent_res.get("mismatch"):
            risk_level = "CRITICAL"
            decision = "PAUSE"
            risk_score = max(risk_score, 0.95)
            claimed = intent_res.get("claimed_action")
            actual = intent_res.get("actual_action")
            amount = txn_features.get("amount", 0.0)
            
            primary_warning = f"STOP — You are about to {actual} ₹{amount:,.0f}, not {claimed} ₹{amount:,.0f}."
            detailed_explanation = (
                f"INTENT MISMATCH DETECTED: The message claims you will {claimed} money, "
                f"but this payment will actually SEND ₹{amount:,.0f} from your bank account."
            )
            voice_summary = f"Stop! You are sending {amount:.0f} rupees, not receiving it. Do not approve this payment."

        # Rule 2: PAYMENT ESCALATION DETECTED
        elif escalation_res.get("escalation_detected"):
            risk_level = "CRITICAL"
            decision = "PAUSE"
            risk_score = max(risk_score, 0.90)
            count = escalation_res.get("payment_count")
            spent = escalation_res.get("total_spent")
            
            primary_warning = "WARNING — Payment Escalation Pattern Detected!"
            detailed_explanation = (
                f"PAYMENT ESCALATION DETECTED: You have made {count} repeated, "
                f"increasing payments totaling ₹{spent:,.0f}. Scammers often ask for multiple "
                "increasing payments under the same promised story."
            )
            voice_summary = f"Warning! You have made {count} repeated increasing payments. Stop paying further fees."

        # Rule 3: ADVANCE FEE / PAYMENT BEFORE BENEFIT
        elif promise_res.get("payment_before_benefit"):
            risk_level = "HIGH"
            decision = "VERIFY"
            risk_score = max(risk_score, 0.75)
            req = promise_res.get("requested_amount", 0)
            prom = promise_res.get("promised_amount", 0)
            ben = promise_res.get("benefit", "promised benefit")
            
            if req and prom:
                primary_warning = f"Be careful! You are asked to pay ₹{req:,.0f} first for a promised ₹{prom:,.0f} {ben}."
                detailed_explanation = (
                    f"ADVANCE-FEE RISK: You are being asked to pay ₹{req:,.0f} first as a "
                    f"{promise_res.get('reason') or 'fee'} to receive a promised ₹{prom:,.0f} {ben}. "
                    "Legitimate organizations rarely demand upfront money to release pensions or prizes."
                )
                voice_summary = f"Be careful! You are paying {req:.0f} rupees upfront for a promised {prom:.0f} rupees {ben}."
            else:
                primary_warning = "Caution — Upfront payment requested before receiving promised benefit."
                detailed_explanation = "You are paying a fee before receiving a promised benefit."
                voice_summary = "Caution! You are paying money upfront before getting your benefit."

        # Rule 4: HIGH SCAM SIGNALS OR THREATS
        elif "threat" in scam_signals or "guaranteed_return" in scam_signals or "suspicious_url" in scam_signals:
            risk_level = "HIGH"
            decision = "VERIFY"
            risk_score = max(risk_score, 0.70)
            primary_warning = "Caution — High-risk fraud signals detected in message."
            detailed_explanation = (
                "Scam signals detected in message context: " + ", ".join(scam_signals) + ". "
                "Verify the sender identity through official customer care channels."
            )
            voice_summary = "Caution! High risk fraud signals detected. Please verify before paying."

        # Rule 5: MEDIUM RISK (New Recipient + Urgency or Unusual Amount)
        elif risk_score >= 0.40:
            risk_level = "MEDIUM"
            decision = "VERIFY"
            primary_warning = "Verification recommended for new payment request."
            detailed_explanation = (
                "This transaction has unusual factors (new recipient or urgent language). "
                "Please confirm the payment details carefully."
            )
            voice_summary = "Please verify the recipient before approving payment."

        # Rule 6: LOW RISK LEGITIMATE
        else:
            risk_level = "LOW"
            decision = "ALLOW"
            risk_score = min(risk_score, 0.20)

        return {
            "risk_level": risk_level,
            "decision": decision,
            "risk_score": risk_score,
            "primary_warning": primary_warning,
            "detailed_explanation": detailed_explanation,
            "voice_summary": voice_summary,
            "signals_triggered": scam_signals,
            "breakdown": {
                "intent": intent_res,
                "promise": promise_res,
                "escalation": escalation_res,
                "transaction_features": txn_features,
                "raw_score": raw_score
            }
        }


def calculate_ml_risk(
    message: Optional[str],
    transaction: Dict[str, Any],
    history: Optional[List[Dict[str, Any]]] = None
) -> Dict[str, Any]:
    """Convenience function for calculating ML risk score."""
    model = BharosaRiskModel()
    return model.calculate_risk(message, transaction, history)
