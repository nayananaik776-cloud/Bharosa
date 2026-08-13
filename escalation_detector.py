"""
Bharosa Fraud Intelligence - Escalation Detector Module
Detects payment escalation patterns where a user is tricked into making
multiple, monotonically increasing payments for the same promised benefit.
"""

from typing import List, Dict, Any, Optional


class EscalationDetector:
    """Class responsible for multi-payment escalation analysis."""

    def detect_escalation(self, history: Optional[List[Dict[str, Any]]]) -> Dict[str, Any]:
        """
        Analyzes transaction history for repeated payments, increasing amounts,
        and cumulative fee escalation.
        """
        if not history or not isinstance(history, list) or len(history) == 0:
            return {
                "escalation_detected": False,
                "escalation_level": "NONE",
                "payment_count": 0,
                "total_spent": 0.0,
                "sequence": [],
                "is_increasing_sequence": False,
                "rationale": "No payment history provided."
            }

        # Filter relevant outgoing payments
        outgoing = []
        for item in history:
            amount = item.get("amount", 0.0)
            try:
                val = float(amount)
                if val > 0:
                    outgoing.append({
                        "amount": val,
                        "recipient": item.get("recipient", item.get("upi_id", "")),
                        "timestamp": item.get("timestamp", ""),
                        "category": item.get("category", item.get("reason", ""))
                    })
            except (ValueError, TypeError):
                continue

        if len(outgoing) == 0:
            return {
                "escalation_detected": False,
                "escalation_level": "NONE",
                "payment_count": 0,
                "total_spent": 0.0,
                "sequence": [],
                "is_increasing_sequence": False,
                "rationale": "No outgoing payments found in history."
            }

        sequence = [p["amount"] for p in outgoing]
        payment_count = len(sequence)
        total_spent = sum(sequence)

        # Check if amounts are monotonically increasing or escalating significantly
        is_increasing = False
        if payment_count >= 2:
            increasing_steps = sum(1 for i in range(len(sequence) - 1) if sequence[i + 1] >= sequence[i])
            is_increasing = (increasing_steps == len(sequence) - 1)

        # Check recipient consistency or shared context
        recipients = [p["recipient"] for p in outgoing if p["recipient"]]
        same_recipient = (len(set(recipients)) == 1) if recipients else False

        # Determine Escalation Risk Level
        escalation_detected = False
        escalation_level = "NONE"
        rationale = "Normal transaction pattern."

        if payment_count >= 3 and is_increasing:
            escalation_detected = True
            escalation_level = "CRITICAL"
            rationale = (
                f"PAYMENT ESCALATION DETECTED — You have made {payment_count} repeated, "
                f"increasing payments totaling ₹{total_spent:,.0f}."
            )
        elif payment_count >= 2 and (is_increasing or same_recipient):
            escalation_detected = True
            escalation_level = "HIGH" if total_spent > 3000 else "MODERATE"
            rationale = (
                f"Repeated payment pattern detected ({payment_count} payments, total ₹{total_spent:,.0f}). "
                "Scammers often ask for multiple small payments before larger demands."
            )
        elif payment_count >= 2:
            escalation_detected = False
            escalation_level = "LOW"
            rationale = f"{payment_count} previous payments recorded in history."

        return {
            "escalation_detected": escalation_detected,
            "escalation_level": escalation_level,
            "payment_count": payment_count,
            "total_spent": total_spent,
            "sequence": sequence,
            "is_increasing_sequence": is_increasing,
            "same_recipient": same_recipient,
            "rationale": rationale
        }


def detect_escalation(history: Optional[List[Dict[str, Any]]]) -> Dict[str, Any]:
    """Convenience function for escalation detection."""
    detector = EscalationDetector()
    return detector.detect_escalation(history)
