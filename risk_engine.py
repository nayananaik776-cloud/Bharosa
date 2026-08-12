from ..schemas import AnalyzeFullRequest, AnalyzeFullResponse, IntentAnalysis, PromiseAnalysis

def evaluate_risk(request: AnalyzeFullRequest) -> AnalyzeFullResponse:
    # ----------------------------------------------------
    # DETERMINISTIC DEMO SCENARIOS
    # ----------------------------------------------------
    msg = request.message_text.lower()
    amount = request.transaction.amount

    # 1. Advance-fee Scam (Pension)
    if "pension" in msg and "verification fee" in msg and amount == 2000:
        return AnalyzeFullResponse(
            risk_score=85,
            risk_level="HIGH",
            decision="PAUSE",
            intent_analysis=IntentAnalysis(detected_intent="PAY_FEE", mismatch=False),
            promise_analysis=PromiseAnalysis(promised_amount=50000, condition="PAYMENT_FIRST"),
            signals=["ADVANCE_FEE_REQUEST", "PROMISE_MISMATCH"],
            explanation="Be careful. You are being asked to pay ₹2,000 first for a promised ₹50,000 benefit.",
            recommended_action="Verify the recipient before proceeding.",
            voice_text="Be careful. You are being asked to pay 2000 rupees first for a promised 50000 rupees benefit."
        )

    # 2. Refund QR Scam (Intent Mismatch)
    if "refund" in msg and "receive" in msg and request.transaction.direction == "SEND":
        return AnalyzeFullResponse(
            risk_score=90,
            risk_level="CRITICAL",
            decision="PAUSE",
            intent_analysis=IntentAnalysis(detected_intent="RECEIVE", mismatch=True),
            promise_analysis=PromiseAnalysis(promised_amount=amount, condition="NONE"),
            signals=["INTENT_MISMATCH", "REFUND_SCAM"],
            explanation=f"STOP — You are about to SEND ₹{amount:,.0f}, not RECEIVE ₹{amount:,.0f}.",
            recommended_action="Do not enter your UPI PIN. This is a scam.",
            voice_text=f"Stop. You are about to send {int(amount)} rupees, not receive {int(amount)} rupees."
        )

    # 3. Escalating Payment Scam
    if request.user_context.previous_related_payments and len(request.user_context.previous_related_payments) > 0:
        if "gst" in msg or "release fee" in msg or "registration" in msg:
            return AnalyzeFullResponse(
                risk_score=95,
                risk_level="CRITICAL",
                decision="PAUSE",
                intent_analysis=IntentAnalysis(detected_intent="PAY_FEE", mismatch=False),
                promise_analysis=PromiseAnalysis(promised_amount=None, condition="ESCALATION"),
                signals=["PAYMENT_ESCALATION", "SUNKEN_COST_TRAP"],
                explanation="Payment escalation detected. You are repeatedly paying for the same promised benefit.",
                recommended_action="Stop paying. This is a common scam pattern.",
                voice_text="Stop paying. You are being asked for repeated payments. This is a scam."
            )

    # 4. Safe Payment (Default)
    return AnalyzeFullResponse(
        risk_score=10,
        risk_level="LOW",
        decision="ALLOW",
        intent_analysis=IntentAnalysis(detected_intent="SEND", mismatch=False),
        promise_analysis=PromiseAnalysis(promised_amount=None, condition=None),
        signals=[],
        explanation="This payment appears safe based on the current context.",
        recommended_action="You may proceed with the payment.",
        voice_text="This payment appears safe."
    )
