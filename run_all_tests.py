"""
Bharosa Fraud Guardian - Unified Test Suite Runner
Runs backend API tests, ML fraud intelligence tests, and Android unit tests without external test runner dependencies.
"""

import unittest
import sys
import os

# Set UTF-8 encoding for Windows stdout
if hasattr(sys.stdout, 'reconfigure'):
    sys.stdout.reconfigure(encoding='utf-8')

# Add root to sys.path
sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))

from ml.interface import evaluate_payment_risk
from ml.intent_analyzer import analyze_intent
from ml.promise_analyzer import analyze_promise
from ml.escalation_detector import detect_escalation
from ml.scam_signals import extract_scam_signals

from android.test_runner import is_relevant_financial


class TestMlScamIntelligence(unittest.TestCase):

    def test_case_1_refund_qr_mismatch(self):
        message = "Your ₹5,000 refund has been approved. Scan this QR to receive your refund."
        transaction = {
            "amount": 5000.0,
            "direction": "SEND",
            "recipient": "scammer_refund@upi",
            "is_new_recipient": True
        }
        intent_res = analyze_intent(message, transaction)
        self.assertIn(intent_res["claimed_action"], ["RECEIVE", "REFUND"])
        self.assertEqual(intent_res["actual_action"], "SEND")
        self.assertTrue(intent_res["mismatch"])

        result = evaluate_payment_risk(message, transaction)
        self.assertEqual(result["risk_level"], "CRITICAL")
        self.assertEqual(result["decision"], "PAUSE")
        self.assertIn("STOP — You are about to SEND", result["primary_warning"])

    def test_case_2_pension_advance_fee(self):
        message = "Your ₹50,000 pension has been approved. Pay ₹2,000 verification fee to receive it."
        transaction = {
            "amount": 2000.0,
            "direction": "SEND",
            "recipient": "epfo_fake_agent@upi",
            "is_new_recipient": True
        }
        promise_res = analyze_promise(message)
        self.assertEqual(promise_res["requested_amount"], 2000.0)
        self.assertEqual(promise_res["promised_amount"], 50000.0)
        self.assertEqual(promise_res["benefit"], "pension")
        self.assertTrue(promise_res["payment_before_benefit"])

        result = evaluate_payment_risk(message, transaction)
        self.assertIn(result["risk_level"], ["HIGH", "CRITICAL"])
        self.assertIn(result["decision"], ["VERIFY", "PAUSE"])

    def test_case_3_escalating_payment(self):
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
        self.assertTrue(escalation_res["escalation_detected"])
        self.assertEqual(escalation_res["payment_count"], 3)

        result = evaluate_payment_risk(message, transaction, history)
        self.assertEqual(result["risk_level"], "CRITICAL")
        self.assertEqual(result["decision"], "PAUSE")

    def test_case_4_legitimate_payment(self):
        message = "Paying ₹450 for monthly grocery bill at Sharma Kirana Store."
        transaction = {
            "amount": 450.0,
            "direction": "SEND",
            "recipient": "sharma_kirana@upi",
            "is_new_recipient": False
        }
        result = evaluate_payment_risk(message, transaction)
        self.assertEqual(result["risk_level"], "LOW")
        self.assertEqual(result["decision"], "ALLOW")
        self.assertLessEqual(result["risk_score"], 0.30)

    def test_case_5_ambiguous_message(self):
        message = "Meeting at 4 PM near central park station."
        transaction = {
            "amount": 200.0,
            "direction": "SEND",
            "recipient": "friend_ramesh@upi",
            "is_new_recipient": False
        }
        intent_res = analyze_intent(message, transaction)
        self.assertEqual(intent_res["claimed_action"], "UNKNOWN")

        result = evaluate_payment_risk(message, transaction)
        self.assertEqual(result["risk_level"], "LOW")
        self.assertEqual(result["decision"], "ALLOW")


class TestAndroidGuardianFiltering(unittest.TestCase):

    def test_financial_notification_filters(self):
        self.assertTrue(is_relevant_financial("com.whatsapp", "Pension Dept", "Pay ₹2,500 verification fee to release pension"))
        self.assertFalse(is_relevant_financial("com.instagram.android", "Friend", "Hey, what's up?"))


if __name__ == "__main__":
    unittest.main()

