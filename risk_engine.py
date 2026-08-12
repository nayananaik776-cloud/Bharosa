from typing import List
import re
from backend.models import (
    AnalysisRequest,
    AnalysisResponse,
    Signal,
    AnalysisDetails
)

def evaluate_risk(request: AnalysisRequest) -> AnalysisResponse:
    msg_lower = request.message.lower()
    tx = request.transaction
    intent = request.intent
    history = request.payment_history or []

    actual_action = tx.type.upper()
    
    # Determine believed action
    believed_action = actual_action
    if intent and intent.believed_action:
        believed_action = intent.believed_action.upper()
    elif "receive" in msg_lower or "refund" in msg_lower or "claim" in msg_lower:
        believed_action = "RECEIVE"

    signals: List[Signal] = []
    
    # 1. INTENT MISMATCH CHECK
    intent_mismatch = (believed_action == "RECEIVE" and actual_action == "SEND")
    if intent_mismatch:
        signals.append(Signal(
            code="INTENT_MISMATCH",
            severity="HIGH",
            description="User believes they are receiving funds, but the transaction sends funds."
        ))

    # 2. ADVANCE-FEE SCAM CHECK
    promised_amount = 0.0
    if intent and intent.promised_amount:
        promised_amount = intent.promised_amount
    else:
        # Regex search for amounts in message like ₹50,000 or 50000
        amounts = [float(x.replace(',', '')) for x in re.findall(r'₹?\s*(\d+(?:,\d+)*)', msg_lower)]
        if len(amounts) > 1:
            promised_amount = max(amounts)
        elif len(amounts) == 1 and amounts[0] > tx.amount:
            promised_amount = amounts[0]

    advance_payment_detected = False
    fee_keywords = ["verification fee", "registration", "tax", "pension", "release fee", "processing fee"]
    if promised_amount > tx.amount and (any(kw in msg_lower for kw in fee_keywords) or (intent and intent.promised_benefit)):
        advance_payment_detected = True
        signals.append(Signal(
            code="ADVANCE_PAYMENT_RISK",
            severity="HIGH",
            description=f"User is requested to pay ₹{tx.amount:,.0f} up-front for a promised benefit of ₹{promised_amount:,.0f}."
        ))
    elif any(kw in msg_lower for kw in ["pension", "lottery", "prize", "gift"]) and tx.amount > 0 and actual_action == "SEND":
        advance_payment_detected = True
        signals.append(Signal(
            code="ADVANCE_PAYMENT_RISK",
            severity="HIGH",
            description=f"User is paying ₹{tx.amount:,.0f} prior to receiving promised pension/benefit."
        ))

    # 3. PAYMENT ESCALATION CHECK
    escalation_detected = False
    if len(history) >= 2:
        amounts = [h.amount for h in history] + [tx.amount]
        # Check if amounts are monotonically non-decreasing
        if sorted(amounts) == amounts and len(set(amounts)) > 1:
            escalation_detected = True
            signals.append(Signal(
                code="PAYMENT_ESCALATION",
                severity="CRITICAL",
                description="Repeated payment attempts detected with escalating payment amounts."
            ))

    # Determine Overall Risk Level and Action
    if escalation_detected:
        risk_level = "CRITICAL"
        action = "PAUSE"
        explanation = f"PAYMENT ESCALATION DETECTED — You have made {len(history)} previous payment(s) of increasing amounts for this offer."
        voice_warning = "Critical Warning: Payment escalation detected. Do not send further payments for this request."
    elif intent_mismatch:
        risk_level = "HIGH"
        action = "PAUSE"
        explanation = f"STOP — You are about to SEND ₹{tx.amount:,.0f}, not RECEIVE ₹{tx.amount:,.0f}."
        voice_warning = f"Warning: You are about to send ₹{tx.amount:,.0f} instead of receiving money. Please stop and verify."
    elif advance_payment_detected:
        risk_level = "HIGH"
        action = "PAUSE"
        promised_str = f"₹{promised_amount:,.0f}" if promised_amount > 0 else "a promised"
        explanation = f"Be careful. You are being asked to pay ₹{tx.amount:,.0f} first for {promised_str} benefit."
        voice_warning = f"Caution: Do not pay ₹{tx.amount:,.0f} up-front to claim a promised benefit."
    else:
        risk_level = "LOW"
        action = "ALLOW"
        explanation = "Payment parameters appear standard. Verify recipient details before confirming."
        voice_warning = "Payment risk is low. Double check recipient name before approving."

    details = AnalysisDetails(
        believed_action=believed_action,
        actual_action=actual_action,
        requested_amount=tx.amount,
        promised_amount=promised_amount,
        advance_payment_detected=advance_payment_detected,
        escalation_detected=escalation_detected
    )

    return AnalysisResponse(
        risk_level=risk_level,
        action=action,
        explanation=explanation,
        signals=signals,
        details=details,
        voice_warning=voice_warning
    )
