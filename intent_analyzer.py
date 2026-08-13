"""
Bharosa Fraud Intelligence - Intent Analyzer Module
Analyzes financial messages to extract the user's perceived intent (claimed action)
and compares it with the actual transaction action to detect Intent Mismatches.
"""

from typing import Dict, Any, Optional

# Supported intent categories
ACTION_RECEIVE = "RECEIVE"
ACTION_SEND = "SEND"
ACTION_REFUND = "REFUND"
ACTION_VERIFY = "VERIFY"
ACTION_REGISTER = "REGISTER"
ACTION_INVEST = "INVEST"
ACTION_WITHDRAW = "WITHDRAW"
ACTION_UNKNOWN = "UNKNOWN"

# Keyword indicators for claimed actions
RECEIVE_KEYWORDS = [
    "receive", "get", "credit", "credited", "collect", "claim", 
    "money added", "cashback received", "deposit to your account",
    "scan to receive", "scan qr to receive", "accept payment"
]

REFUND_KEYWORDS = [
    "refund", "refunded", "reimbursement", "cashback",
    "refund approved", "claim refund", "get refund", "return money"
]

SEND_KEYWORDS = [
    "send", "pay", "paying", "transfer", "debit", "remit",
    "send money", "pay fee", "scan to pay", "make payment"
]

VERIFY_KEYWORDS = [
    "verify", "verification", "kyc", "confirm account",
    "verification fee", "identity check", "account check"
]

REGISTER_KEYWORDS = [
    "register", "registration", "signup", "enrolment",
    "registration fee", "joining fee"
]

INVEST_KEYWORDS = [
    "invest", "investment", "guaranteed return", "daily return",
    "double your money", "profit share", "trading bot"
]

WITHDRAW_KEYWORDS = [
    "withdraw", "withdrawal", "cash out", "payout"
]


class IntentAnalyzer:
    """Class responsible for Intent Analysis and Intent Mismatch Detection."""

    def extract_claimed_action(self, message: Optional[str]) -> str:
        """
        Extracts the perceived financial action from a text message.
        Returns UNKNOWN if ambiguous or no clear financial action is present.
        """
        if not message or not isinstance(message, str):
            return ACTION_UNKNOWN

        text = message.lower().strip()
        if not text:
            return ACTION_UNKNOWN

        # Find position of earliest keyword match for each category
        categories = [
            (ACTION_REFUND, REFUND_KEYWORDS),
            (ACTION_RECEIVE, RECEIVE_KEYWORDS),
            (ACTION_VERIFY, VERIFY_KEYWORDS),
            (ACTION_REGISTER, REGISTER_KEYWORDS),
            (ACTION_INVEST, INVEST_KEYWORDS),
            (ACTION_WITHDRAW, WITHDRAW_KEYWORDS),
            (ACTION_SEND, SEND_KEYWORDS),
        ]

        matches = []
        for action, keywords in categories:
            for kw in keywords:
                pos = text.find(kw)
                if pos != -1:
                    matches.append((pos, action, kw))

        if not matches:
            return ACTION_UNKNOWN

        # Sort by match position in text (earliest action verb in the sentence wins)
        matches.sort(key=lambda x: x[0])
        return matches[0][1]

    def analyze_intent(self, message: Optional[str], transaction: Dict[str, Any]) -> Dict[str, Any]:
        """
        Compares claimed action from message with actual transaction parameters.
        Returns structured analysis including mismatch detection.
        """
        claimed_action = self.extract_claimed_action(message)
        
        # Determine actual action from transaction object
        direction = str(transaction.get("direction", "")).upper()
        txn_type = str(transaction.get("type", "")).upper()
        
        if direction == "SEND" or txn_type in ["PAY", "DEBIT", "TRANSFER"]:
            actual_action = ACTION_SEND
        elif direction == "RECEIVE" or txn_type in ["CREDIT", "COLLECT"]:
            actual_action = ACTION_RECEIVE
        else:
            actual_action = ACTION_SEND  # Default for UPI outbound payments if unspecified

        # Detect mismatch (e.g. user thinks they are receiving/getting refund, but transaction sends money)
        mismatch = False
        mismatch_type = None

        if claimed_action in [ACTION_RECEIVE, ACTION_REFUND] and actual_action == ACTION_SEND:
            mismatch = True
            mismatch_type = "RECEIVE_VS_SEND_MISMATCH"
        elif claimed_action == ACTION_WITHDRAW and actual_action == ACTION_SEND:
            mismatch = True
            mismatch_type = "WITHDRAW_VS_SEND_MISMATCH"

        confidence = 0.95 if claimed_action != ACTION_UNKNOWN else 0.50

        return {
            "claimed_action": claimed_action,
            "actual_action": actual_action,
            "mismatch": mismatch,
            "mismatch_type": mismatch_type,
            "confidence": confidence,
            "warning": (
                f"STOP — You are about to {actual_action} money, but the message promised to {claimed_action} money."
                if mismatch else None
            )
        }


def analyze_intent(message: Optional[str], transaction: Dict[str, Any]) -> Dict[str, Any]:
    """Convenience function for intent analysis."""
    analyzer = IntentAnalyzer()
    return analyzer.analyze_intent(message, transaction)
