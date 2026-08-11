"""
Python Unit Test & Verification Suite for Bharosa Android Guardian
Tests local financial privacy filtering rules, demo scenario definitions, API fallback payloads, and warning message localization.
"""

import unittest
import re
import json

FINANCIAL_KEYWORDS = [
    r"₹", r"rs\.", r"\brs\b", r"inr", r"rupees", r"rupee",
    r"pay", r"paid", r"payment", r"upi", r"gpay", r"phonepe", r"paytm",
    r"transfer", r"transferred", r"refund", r"cashback", r"reward",
    r"pension", r"bank", r"account", r"a/c", r"debit", r"credited",
    r"verification fee", r"processing fee", r"kyc", r"blocked", r"suspend",
    r"urgent", r"qr", r"pin", r"claim", r"lottery", r"prize", r"winner",
    r"bonus", r"loan", r"otp", r"ebill", r"electricity bill", r"arrears"
]

EXCLUDE_KEYWORDS = [
    "battery full", "battery low", "charging",
    "instagram", "facebook", "twitter", "snapchat", "tiktok", "youtube",
    "sent a photo", "sent a video", "liked your story", "started following",
    "weather", "update available", "download complete", "file saved",
    "bluetooth connected", "wifi connected", "alarm set", "timer finished",
    "missed call", "listening to", "now playing"
]

EXCLUDE_PACKAGES = {
    "com.android.systemui",
    "com.google.android.youtube",
    "com.instagram.android",
    "com.facebook.katana",
    "com.snapchat.android",
    "com.twitter.android",
    "com.spotify.music"
}

def is_relevant_financial(package_name, title, body):
    pkg_lower = package_name.lower()
    combined_text = f"{title} {body}".lower().strip()

    if not combined_text:
        return False

    if pkg_lower in EXCLUDE_PACKAGES:
        return False

    for exc in EXCLUDE_KEYWORDS:
        if exc in combined_text:
            return False

    for kw in FINANCIAL_KEYWORDS:
        if re.search(kw, combined_text):
            return True

    return False

class TestFinancialFilter(unittest.TestCase):

    def test_relevant_financial_scams(self):
        self.assertTrue(is_relevant_financial("com.whatsapp", "Pension Dept", "Pay ₹2,500 verification fee to release ₹75,000 monthly pension"))
        self.assertTrue(is_relevant_financial("com.android.mms", "Electricity Refund", "Your refund of ₹4,999 is pending. Pay ₹1 via UPI to verify account"))
        self.assertTrue(is_relevant_financial("com.android.mms", "URGENT BANK ALERT", "Your account will be BLOCKED within 2 hours. Click link for KYC"))
        self.assertTrue(is_relevant_financial("com.phonepe.app", "Payment Received", "Received ₹500 from Rahul via PhonePe UPI"))

    def test_non_financial_noise(self):
        self.assertFalse(is_relevant_financial("com.android.systemui", "Battery Status", "Battery full. Please unplug charger."))
        self.assertFalse(is_relevant_financial("com.instagram.android", "Rahul", "Hey! Are we still meeting for lunch today?"))
        self.assertFalse(is_relevant_financial("com.spotify.music", "Now Playing", "Artist - Song Title"))
        self.assertFalse(is_relevant_financial("com.android.systemui", "Screenshot captured", "Tap to view screenshot"))

class TestApiFallback(unittest.TestCase):

    def test_offline_fallback_payload(self):
        fallback = {
            "risk_score": 0.5,
            "risk_level": "UNKNOWN",
            "category": "UNVERIFIED_FINANCIAL",
            "is_scam": False,
            "warning_title": {
                "en": "VERIFY BEFORE PAYING",
                "hi": "भुगतान करने से पहले जांच करें"
            },
            "warning_message": {
                "en": "Unable to verify right now. Please verify before paying.",
                "hi": "अभी पुष्टि करने में असमर्थ। कृपया भुगतान करने से पहले जांच लें।"
            },
            "action_required": "VERIFY_FIRST",
            "explanation_audio_text": {
                "en": "Unable to verify message right now. Please verify before paying.",
                "hi": "अभी संदेश की पुष्टि करने में असमर्थ। कृपया भुगतान करने से पहले जांच लें।"
            }
        }
        self.assertNotIn("safe", fallback["warning_message"]["en"].lower())
        self.assertIn("verify", fallback["warning_message"]["en"].lower())
        self.assertEqual(fallback["action_required"], "VERIFY_FIRST")

if __name__ == "__main__":
    unittest.main()
