"""
Bharosa Fraud Intelligence - Transaction Feature Extractor Module
Engineers transaction and behavioral feature vectors for ML model scoring.
"""

from typing import Dict, Any, List, Optional
import datetime


class TransactionFeatureExtractor:
    """Class responsible for engineering transaction and behavioral features."""

    def extract_features(
        self,
        transaction: Dict[str, Any],
        history: Optional[List[Dict[str, Any]]] = None
    ) -> Dict[str, Any]:
        """
        Extracts numerical and categorical feature dictionary from current transaction
        and optional historical transaction array.
        """
        raw_amount = transaction.get("amount", 0.0)
        try:
            amount = float(raw_amount)
        except (ValueError, TypeError):
            amount = 0.0

        recipient = transaction.get("recipient", transaction.get("upi_id", ""))
        direction = str(transaction.get("direction", "SEND")).upper()

        history = history or []

        # Analyze recipient history
        recipient_txns = [t for t in history if t.get("recipient") == recipient or t.get("upi_id") == recipient]
        recipient_history_count = len(recipient_txns)
        is_new_recipient = transaction.get("is_new_recipient", recipient_history_count == 0)

        # Historical amount distribution analysis
        historical_amounts = [float(t.get("amount", 0.0)) for t in history if float(t.get("amount", 0.0)) > 0]
        avg_historical_amount = sum(historical_amounts) / len(historical_amounts) if historical_amounts else amount
        
        is_unusual_amount = (amount > 3.0 * avg_historical_amount) if (historical_amounts and amount > 500) else False

        # Time-based analysis
        timestamp_str = transaction.get("timestamp", "")
        is_unusual_time = False
        if timestamp_str:
            try:
                dt = datetime.datetime.fromisoformat(timestamp_str.replace("Z", "+00:00"))
                hour = dt.hour
                is_unusual_time = (hour >= 23 or hour <= 4)
            except Exception:
                pass

        # Velocity & Frequency
        transaction_frequency = len(history)
        velocity_score = min(amount * (transaction_frequency + 1) / 1000.0, 10.0)
        previous_related_count = len([t for t in history if t.get("category") == transaction.get("category")])

        return {
            "amount": amount,
            "direction": direction,
            "recipient": recipient,
            "is_new_recipient": is_new_recipient,
            "recipient_history_count": recipient_history_count,
            "avg_historical_amount": round(avg_historical_amount, 2),
            "is_unusual_amount": is_unusual_amount,
            "is_unusual_time": is_unusual_time,
            "transaction_frequency": transaction_frequency,
            "velocity_score": round(velocity_score, 2),
            "previous_related_count": previous_related_count
        }


def extract_transaction_features(
    transaction: Dict[str, Any],
    history: Optional[List[Dict[str, Any]]] = None
) -> Dict[str, Any]:
    """Convenience function for feature extraction."""
    extractor = TransactionFeatureExtractor()
    return extractor.extract_features(transaction, history)
