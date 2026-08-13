"""
Bharosa Fraud Intelligence - Reproducible Synthetic Dataset Generator
Generates synthetic transaction and message samples for prototype testing and evaluation.

IMPORTANT NOTICE:
This synthetic dataset is for prototype demonstration purposes only.
It is generated deterministically with fixed random seeds and does NOT contain real banking data.
"""

import os
import random
import pandas as pd
from typing import List, Dict, Any

# Fixed random seed for reproducibility
RANDOM_SEED = 42


def generate_synthetic_dataset(output_path: str = "ml/synthetic_dataset.csv", num_samples: int = 120) -> pd.DataFrame:
    """Generates synthetic dataset covering all core fraud scenarios."""
    random.seed(RANDOM_SEED)

    samples: List[Dict[str, Any]] = []

    # Scenario 1: Refund Scams (QR Receive vs Send Mismatch)
    refund_messages = [
        "Your ₹5,000 refund has been approved. Scan this QR code to receive your money.",
        "Airtel recharge cashback ₹300 pending. Click to collect your refund now.",
        "Amazon ₹1,500 refund issued. Receive payment in your account by scanning QR.",
        "Electricity overcharge refund ₹850 approved. Accept payment via UPI QR."
    ]
    for i in range(25):
        msg = random.choice(refund_messages)
        amount = float(random.choice([300, 500, 850, 1500, 3000, 5000]))
        samples.append({
            "sample_id": f"SYN_REFUND_{i+1:03d}",
            "scenario_type": "refund_scam",
            "message": msg,
            "amount": amount,
            "direction": "SEND",
            "recipient": f"refund_scammer_{i%5}@upi",
            "is_new_recipient": True,
            "history_count": 0,
            "claimed_action": "RECEIVE",
            "actual_action": "SEND",
            "intent_mismatch": True,
            "payment_before_benefit": False,
            "escalation_level": "NONE",
            "true_risk_level": "CRITICAL",
            "true_decision": "PAUSE"
        })

    # Scenario 2: Advance-Fee Scams (Pension, Prize, Job)
    advance_messages = [
        "Your ₹50,000 pension has been approved. Pay ₹2,000 verification fee to receive it.",
        "Congratulations! You won ₹1,00,000 KBC lottery. Pay ₹5,000 GST clearance tax to release prize.",
        "Work from home job selected! Pay ₹1,000 registration fee to get your ₹25,000 monthly salary.",
        "PM Kisan Yojna ₹10,000 grant sanctioned. Deposit ₹500 file processing fee immediately."
    ]
    for i in range(25):
        msg = random.choice(advance_messages)
        amounts = [int(s) for s in msg.replace(",", "").split() if s.isdigit()]
        req = min(amounts) if len(amounts) >= 2 else 2000
        samples.append({
            "sample_id": f"SYN_ADVANCE_{i+1:03d}",
            "scenario_type": "advance_fee_scam",
            "message": msg,
            "amount": float(req),
            "direction": "SEND",
            "recipient": f"govt_claim_fake_{i%4}@upi",
            "is_new_recipient": True,
            "history_count": 0,
            "claimed_action": "VERIFY",
            "actual_action": "SEND",
            "intent_mismatch": False,
            "payment_before_benefit": True,
            "escalation_level": "NONE",
            "true_risk_level": "HIGH",
            "true_decision": "VERIFY"
        })

    # Scenario 3: Payment Escalation Scams (Repeated Increasing Payments)
    escalation_messages = [
        "Pay ₹10,000 final release fee to receive promised ₹2,00,000 grant fund.",
        "Pay ₹5,000 customs GST charge to release your blocked prize package.",
        "Verification fee ₹2,000 paid. Now pay ₹5,000 income tax clearance fee."
    ]
    for i in range(20):
        msg = random.choice(escalation_messages)
        samples.append({
            "sample_id": f"SYN_ESCALATION_{i+1:03d}",
            "scenario_type": "escalation_scam",
            "message": msg,
            "amount": float(random.choice([5000, 7500, 10000, 15000])),
            "direction": "SEND",
            "recipient": f"escalation_agent_{i%3}@upi",
            "is_new_recipient": False,
            "history_count": 3,
            "claimed_action": "VERIFY",
            "actual_action": "SEND",
            "intent_mismatch": False,
            "payment_before_benefit": True,
            "escalation_level": "CRITICAL",
            "true_risk_level": "CRITICAL",
            "true_decision": "PAUSE"
        })

    # Scenario 4: Legitimate Payments (Grocery, Bills, Peer Transfers)
    legit_messages = [
        "Paying ₹450 for monthly grocery store bill at Sharma Kirana Store.",
        "Electricity bill payment ₹1,200 due today for consumer account #9842.",
        "Sent ₹500 to Ramesh for lunch bill splitting.",
        "Paid ₹2,500 monthly milk supply bill via UPI.",
        "Paying ₹150 for tea and snacks."
    ]
    for i in range(35):
        msg = random.choice(legit_messages)
        amount = float(random.choice([150, 450, 500, 1200, 2500]))
        samples.append({
            "sample_id": f"SYN_LEGIT_{i+1:03d}",
            "scenario_type": "legitimate",
            "message": msg,
            "amount": amount,
            "direction": "SEND",
            "recipient": f"trusted_merchant_{i%10}@upi",
            "is_new_recipient": False,
            "history_count": 8,
            "claimed_action": "SEND",
            "actual_action": "SEND",
            "intent_mismatch": False,
            "payment_before_benefit": False,
            "escalation_level": "NONE",
            "true_risk_level": "LOW",
            "true_decision": "ALLOW"
        })

    # Scenario 5: Ambiguous / Unknown Intent Messages
    ambiguous_messages = [
        "Meeting at 4 PM near central park station.",
        "Good morning! Have a great day ahead.",
        "Please call back when you are free.",
        "Order #8492 has been dispatched by courier."
    ]
    for i in range(15):
        msg = random.choice(ambiguous_messages)
        samples.append({
            "sample_id": f"SYN_AMBIGUOUS_{i+1:03d}",
            "scenario_type": "ambiguous",
            "message": msg,
            "amount": float(random.choice([200, 500, 1000])),
            "direction": "SEND",
            "recipient": f"friend_{i%5}@upi",
            "is_new_recipient": False,
            "history_count": 2,
            "claimed_action": "UNKNOWN",
            "actual_action": "SEND",
            "intent_mismatch": False,
            "payment_before_benefit": False,
            "escalation_level": "NONE",
            "true_risk_level": "LOW",
            "true_decision": "ALLOW"
        })

    df = pd.DataFrame(samples)
    
    # Save CSV
    os.makedirs(os.path.dirname(output_path), exist_ok=True)
    df.to_csv(output_path, index=False)
    print(f"Synthetic dataset successfully generated at {output_path} with {len(df)} samples.")
    return df


if __name__ == "__main__":
    generate_synthetic_dataset()
