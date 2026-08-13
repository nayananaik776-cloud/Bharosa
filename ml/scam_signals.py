"""
Bharosa Fraud Intelligence - Scam Signals Extractor Module
Extracts financial scam signals and indicators from messages and transaction context.
"""

import re
from typing import List, Dict, Any, Optional

SIGNAL_PATTERNS = {
    "urgency": [r"\burgent\b", r"\bimmediately\b", r"within \d+ mins?", r"within \d+ hours?", r"today only", r"expir(e|ing)", r"last chance"],
    "threat": [r"\barrest\b", r"\bpolice\b", r"\blegal action\b", r"\bcourt\b", r"\bpenalty\b", r"\bdisconnect(ed)?\b", r"\bblock(ed)?\b"],
    "refund": [r"\brefund(ed)?\b", r"\bcashback\b", r"\breimbursement\b", r"return money"],
    "reward": [r"\breward\b", r"\bbonus\b", r"\bjackpot\b", r"\bwon\b"],
    "pension": [r"\bpension\b", r"retirement fund", r"\bepfo\b"],
    "subsidy": [r"pm kisan", r"\bsubsidy\b", r"\bscheme\b", r"\byojana\b"],
    "verification_fee": [r"verification fee", r"verify fee", r"kyc (charge|fee)", r"identity fee"],
    "processing_fee": [r"processing fee", r"file (charge|fee)", r"handling (charge|fee)"],
    "gst_claim": [r"\bgst\b", r"\btax\b", r"customs duty", r"clearance tax"],
    "release_fee": [r"release fee", r"unblock fee", r"security (fee|deposit)"],
    "prize": [r"\bprize\b", r"\blottery\b", r"lucky draw", r"\bkbc\b"],
    "investment_promise": [r"investment", r"guaranteed return", r"double (your )?money", r"daily profit"],
    "guaranteed_return": [r"100% return", r"zero risk", r"guaranteed (profit|income)"],
    "suspicious_url": [r"http[s]?://", r"\.apk\b", r"bit\.ly", r"tinyurl", r"ngrok"]
}

# Signal Weights for Contextual Risk Scoring
SIGNAL_WEIGHTS = {
    "urgency": 1.5,
    "threat": 2.5,
    "refund": 1.0,
    "reward": 1.2,
    "pension": 1.5,
    "subsidy": 1.2,
    "verification_fee": 2.0,
    "processing_fee": 1.8,
    "gst_claim": 1.8,
    "release_fee": 2.2,
    "prize": 1.5,
    "investment_promise": 2.0,
    "guaranteed_return": 2.5,
    "unknown_recipient": 1.2,
    "payment_before_benefit": 3.0,
    "suspicious_url": 2.5
}


class ScamSignalExtractor:
    """Class responsible for extracting scam signals from text and transaction context."""

    def extract_scam_signals(self, message: Optional[str], transaction: Optional[Dict[str, Any]] = None) -> List[str]:
        """
        Returns a list of identified scam signal identifiers.
        """
        signals = set()
        
        if message and isinstance(message, str):
            text = message.lower().strip()
            for signal_id, patterns in SIGNAL_PATTERNS.items():
                if any(re.search(p, text) for p in patterns):
                    signals.add(signal_id)

        # Check transaction context signals
        if transaction and isinstance(transaction, dict):
            if transaction.get("is_new_recipient", False) or transaction.get("new_recipient", False):
                signals.add("unknown_recipient")
            if transaction.get("payment_before_benefit", False):
                signals.add("payment_before_benefit")

        return sorted(list(signals))

    def calculate_signal_score(self, signals: List[str]) -> float:
        """Calculates aggregated risk score contribution from signals (0.0 to 10.0 scale)."""
        score = sum(SIGNAL_WEIGHTS.get(sig, 1.0) for sig in signals)
        return min(score, 10.0)


def extract_scam_signals(message: Optional[str], transaction: Optional[Dict[str, Any]] = None) -> List[str]:
    """Convenience function for extracting scam signals."""
    extractor = ScamSignalExtractor()
    return extractor.extract_scam_signals(message, transaction)
