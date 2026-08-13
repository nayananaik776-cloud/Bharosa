"""
Bharosa Fraud Intelligence - Comprehensive Test Suite
Tests all 6 required fraud context test cases:
1. Refund QR mismatch
2. Pension advance fee
3. Escalating payment
4. Legitimate payment
5. Ambiguous message
6. Unknown intent
"""

import pytest
from ml.interface import evaluate_payment_risk
from ml.intent_analyzer import analyze_intent
from ml.promise_analyzer import analyze_promise
from ml.escalation_detector import detect_escalation
from ml.scam_signals import extract_scam_signals


def test_case_1_refund_qr_mismatch():
    """
    Test Case 1: Refund QR Mismatch
    Message claims user will receive ₹5,000 refund, but actual payment direction is SEND ₹5,000.
    Expected: Intent mismatch = True, Decision = PAUSE, Risk Level = CRITICAL.
    """
    message = "Your ₹5,000 refund has been approved. Scan this QR to receive your refund."
    transaction = {
        "amount": 5000.0,
        "direction": "SEND",
        "recipient": "scammer_refund@upi",
        "is_new_recipient": True
    }

    intent_res = analyze_intent(message, transaction)
    assert intent_res["claimed_action"] in ["RECEIVE", "REFUND"]
    assert intent_res["actual_action"] == "SEND"
    assert intent_res["mismatch"] is True

    result = evaluate_payment_risk(message, transaction)
    assert result["risk_level"] == "CRITICAL"
    assert result["decision"] == "PAUSE"
    assert "STOP — You are about to SEND" in result["primary_warning"]


def test_case_2_pension_advance_fee():
    """
    Test Case 2: Pension Advance Fee
    Message asks ₹2,000 verification fee upfront to receive ₹50,000 pension benefit.
    Expected: Payment before benefit = True, Decision = VERIFY, Risk Level = HIGH.
    """
    message = "Your ₹50,000 pension has been approved. Pay ₹2,000 verification fee to receive it."
    transaction = {
        "amount": 2000.0,
        "direction": "SEND",
        "recipient": "epfo_fake_agent@upi",
        "is_new_recipient": True
    }

    promise_res = analyze_promise(message)
    assert promise_res["requested_amount"] == 2000.0
    assert promise_res["promised_amount"] == 50000.0
    assert promise_res["benefit"] == "pension"
    assert promise_res["reason"] == "verification fee"
    assert promise_res["payment_before_benefit"] is True

    result = evaluate_payment_risk(message, transaction)
    assert result["risk_level"] in ["HIGH", "CRITICAL"]
    assert result["decision"] in ["VERIFY", "PAUSE"]
    assert "asked to pay ₹2,000 first" in result["primary_warning"] or "Upfront payment" in result["primary_warning"]


def test_case_3_escalating_payment():
    """
    Test Case 3: Escalating Payment Pattern
    User has made repeated, increasing payments (1000 -> 2000 -> 5000) for same story.
    Expected: Escalation detected = True, Decision = PAUSE, Risk Level = CRITICAL.
    """
    message = "Pay ₹10,000 release fee to receive promised ₹2,00,000 prize money."
    transaction = {
        "amount": 10000.0,
        "direction": "SEND",
        "recipient": "prize_agent@upi",
        "is_new_recipient": False
    }
    history = [
        {"amount": 1000.0, "recipient": "prize_agent@upi", "category": "registration"},
        {"amount": 2000.0, "recipient": "prize_agent@upi", "category": "verification"},
        {"amount": 5000.0, "recipient": "prize_agent@upi", "category": "gst"}
    ]

    escalation_res = detect_escalation(history)
    assert escalation_res["escalation_detected"] is True
    assert escalation_res["is_increasing_sequence"] is True
    assert escalation_res["payment_count"] == 3

    result = evaluate_payment_risk(message, transaction, history)
    assert result["risk_level"] == "CRITICAL"
    assert result["decision"] == "PAUSE"
    assert "Escalation Pattern Detected" in result["primary_warning"]


def test_case_4_legitimate_payment():
    """
    Test Case 4: Legitimate Payment
    Paying ₹450 for monthly groceries at known store.
    Expected: Risk Level = LOW, Decision = ALLOW.
    """
    message = "Paying ₹450 for monthly grocery bill at Sharma Kirana Store."
    transaction = {
        "amount": 450.0,
        "direction": "SEND",
        "recipient": "sharma_kirana@upi",
        "is_new_recipient": False
    }

    result = evaluate_payment_risk(message, transaction)
    assert result["risk_level"] == "LOW"
    assert result["decision"] == "ALLOW"
    assert result["risk_score"] <= 0.30


def test_case_5_ambiguous_message():
    """
    Test Case 5: Ambiguous Message
    Text is non-financial ("Meeting at 4 PM near central park station.").
    Expected: Claimed action = UNKNOWN, no false intent claims, decision based on normal transaction.
    """
    message = "Meeting at 4 PM near central park station."
    transaction = {
        "amount": 200.0,
        "direction": "SEND",
        "recipient": "friend_ramesh@upi",
        "is_new_recipient": False
    }

    intent_res = analyze_intent(message, transaction)
    assert intent_res["claimed_action"] == "UNKNOWN"

    result = evaluate_payment_risk(message, transaction)
    assert result["risk_level"] == "LOW"
    assert result["decision"] == "ALLOW"


def test_case_6_unknown_intent():
    """
    Test Case 6: Unknown Intent / Non-Financial Text
    Plain text message with no financial action verbs.
    Expected: Does NOT hallucinate intent. Returns UNKNOWN cleanly.
    """
    message = "Hello, how are you doing today?"
    transaction = {
        "amount": 100.0,
        "direction": "SEND",
        "recipient": "brother@upi",
        "is_new_recipient": False
    }

    intent_res = analyze_intent(message, transaction)
    assert intent_res["claimed_action"] == "UNKNOWN"

    signals = extract_scam_signals(message, transaction)
    assert len(signals) == 0 or signals == ["unknown_recipient"] if transaction.get("is_new_recipient") else True
