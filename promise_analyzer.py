"""
Bharosa Fraud Intelligence - Promise Analyzer Module
Extracts monetary promises, requested fees, fee reasons, organization claims,
and payment-before-benefit patterns from text messages.
"""

import re
from typing import Dict, Any, Optional, List

BENEFIT_PATTERNS = {
    "pension": [r"pension", r"retirement fund", r"provident fund", r"epfo"],
    "refund": [r"refund", r"cashback", r"reimbursement", r"return money"],
    "subsidy": [r"subsidy", r"pm kisan", r"scheme", r"government assistance", r"yojana"],
    "lottery": [r"lottery", r"prize", r"lucky draw", r"kbc", r"jackpot", r"winner"],
    "job": [r"job", r"employment", r"salary", r"work from home", r"joining bonus"],
    "loan": [r"loan", r"credit line", r"instant loan", r"approval"],
    "grant": [r"grant", r"financial aid", r"scholarship"]
}

REASON_PATTERNS = {
    "verification fee": [r"verification", r"verify account", r"identity check", r"kyc fee"],
    "processing fee": [r"processing", r"file charge", r"handling fee", r"service fee"],
    "gst claim": [r"gst", r"tax", r"duty", r"customs charge"],
    "release fee": [r"release", r"clearance", r"unblock fee", r"security deposit"],
    "registration fee": [r"registration", r"entry fee", r"enrolment fee"]
}

ORGANIZATION_PATTERNS = {
    "Government / Scheme": [r"pm kisan", r"epfo", r"gov", r"sarkar", r"ministry", r"rbi", r"income tax"],
    "Bank": [r"sbi", r"hdfc", r"icici", r"axis", r"bank", r"pnb"],
    "Telecom": [r"jio", r"airtel", r"vi", r"bsnl"],
    "Prize / Lottery": [r"kbc", r"lottery", r"dream11", r"lucky winner"]
}

URGENCY_KEYWORDS = [
    "urgent", "immediately", "today only", "within 10 mins", "within 1 hour",
    "expire", "expiring", "last chance", "account block", "suspended", "action required"
]


class PromiseAnalyzer:
    """Class responsible for Promise and Advance-Fee Analysis."""

    def _extract_amounts(self, text: str) -> List[float]:
        """Extracts numerical currency values (₹, INR, Rs) from text."""
        # Clean text and search for currency patterns
        cleaned = re.sub(r',', '', text)
        patterns = [
            r'(?:₹|inr|rs\.?|rs)\s*(\d+(?:\.\d{1,2})?)',
            r'(\d+(?:\.\d{1,2})?)\s*(?:rupees|rs|inr)'
        ]
        
        amounts = []
        for pat in patterns:
            matches = re.findall(pat, cleaned, re.IGNORECASE)
            for m in matches:
                try:
                    val = float(m)
                    if val > 0 and val not in amounts:
                        amounts.append(val)
                except ValueError:
                    pass

        return sorted(amounts)

    def analyze_promise(self, message: Optional[str]) -> Dict[str, Any]:
        """
        Analyzes a text message to extract requested payments vs promised benefits.
        """
        if not message or not isinstance(message, str):
            return {
                "requested_amount": None,
                "promised_amount": None,
                "benefit": None,
                "reason": None,
                "payment_before_benefit": False,
                "organization_claim": None,
                "urgency": False,
                "scam_type_detected": None
            }

        text = message.lower().strip()
        amounts = self._extract_amounts(text)

        requested_amount = None
        promised_amount = None

        # Determine requested vs promised amount logic
        if len(amounts) >= 2:
            # Usually requested amount is smaller than promised amount (e.g. Pay 2000 to get 50000)
            requested_amount = min(amounts)
            promised_amount = max(amounts)
        elif len(amounts) == 1:
            # Single amount: check context to see if requested or promised
            val = amounts[0]
            if any(term in text for term in ["pay", "fee", "charge", "deposit", "send"]):
                requested_amount = val
            elif any(term in text for term in ["approved", "receive", "won", "credited"]):
                promised_amount = val

        # Identify benefit type
        benefit = None
        for b_name, patterns in BENEFIT_PATTERNS.items():
            if any(re.search(p, text) for p in patterns):
                benefit = b_name
                break

        # Identify fee reason
        reason = None
        for r_name, patterns in REASON_PATTERNS.items():
            if any(re.search(p, text) for p in patterns):
                reason = r_name
                break

        # Identify organization claim
        org_claim = None
        for o_name, patterns in ORGANIZATION_PATTERNS.items():
            if any(re.search(p, text) for p in patterns):
                org_claim = o_name
                break

        # Urgency detection
        urgency = any(uk in text for uk in URGENCY_KEYWORDS)

        # Detect Payment-Before-Benefit pattern
        payment_before_benefit = False
        scam_type = None

        if requested_amount and promised_amount and promised_amount > requested_amount:
            payment_before_benefit = True
            scam_type = "ADVANCE_FEE_SCAM"
        elif requested_amount and benefit and benefit not in ["refund"]:
            payment_before_benefit = True
            scam_type = "UPFRONT_FEE_FOR_BENEFIT"

        return {
            "requested_amount": requested_amount,
            "promised_amount": promised_amount,
            "benefit": benefit,
            "reason": reason,
            "payment_before_benefit": payment_before_benefit,
            "organization_claim": org_claim,
            "urgency": urgency,
            "scam_type_detected": scam_type,
            "explanation": (
                f"Be careful. You are being asked to pay ₹{requested_amount:,.0f} first for a promised ₹{promised_amount:,.0f} {benefit or 'benefit'}."
                if payment_before_benefit and requested_amount and promised_amount else None
            )
        }


def analyze_promise(message: Optional[str]) -> Dict[str, Any]:
    """Convenience function for promise analysis."""
    analyzer = PromiseAnalyzer()
    return analyzer.analyze_promise(message)
