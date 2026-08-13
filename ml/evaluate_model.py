"""
Bharosa Fraud Intelligence - Model Evaluation Script
Evaluates the performance of the Bharosa Fraud Intelligence Layer using actual measured metrics
(Precision, Recall, F1-Score, Confusion Matrix).
"""

import os
import pandas as pd
from sklearn.metrics import classification_report, confusion_matrix, precision_recall_fscore_support
from ml.dataset import generate_synthetic_dataset
from ml.risk_model import BharosaRiskModel


def evaluate_bharosa_model(dataset_path: str = "ml/synthetic_dataset.csv") -> dict:
    """Evaluates Bharosa risk predictions against ground truth labels."""
    if not os.path.exists(dataset_path):
        print(f"Dataset not found at {dataset_path}. Generating synthetic dataset...")
        df = generate_synthetic_dataset(dataset_path)
    else:
        df = pd.read_csv(dataset_path)

    model = BharosaRiskModel()

    y_true_risk = []
    y_pred_risk = []

    y_true_decision = []
    y_pred_decision = []

    # Map sample history for escalation test samples
    escalation_history = [
        {"amount": 1000, "recipient": "escalation_agent_0@upi", "category": "verification"},
        {"amount": 2000, "recipient": "escalation_agent_0@upi", "category": "verification"},
        {"amount": 5000, "recipient": "escalation_agent_0@upi", "category": "verification"}
    ]

    for idx, row in df.iterrows():
        msg = str(row["message"])
        txn = {
            "amount": float(row["amount"]),
            "direction": str(row["direction"]),
            "recipient": str(row["recipient"]),
            "is_new_recipient": bool(row["is_new_recipient"])
        }

        # Provide history for escalation scenarios
        history = escalation_history if row["scenario_type"] == "escalation_scam" else []

        res = model.calculate_risk(msg, txn, history)

        y_true_risk.append(str(row["true_risk_level"]).upper())
        y_pred_risk.append(str(res["risk_level"]).upper())

        y_true_decision.append(str(row["true_decision"]).upper())
        y_pred_decision.append(str(res["decision"]).upper())

    # Compute actual empirical metrics
    precision, recall, f1, _ = precision_recall_fscore_support(
        y_true_decision, y_pred_decision, average="weighted", zero_division=0
    )

    conf_matrix = confusion_matrix(y_true_decision, y_pred_decision, labels=["ALLOW", "VERIFY", "PAUSE"])
    report_str = classification_report(y_true_decision, y_pred_decision, zero_division=0)

    print("==================================================")
    print("BHAROSA FRAUD INTELLIGENCE - EVALUATION REPORT")
    print("==================================================")
    print(f"Total Evaluated Samples: {len(df)}")
    print(f"Weighted Precision: {precision:.4f}")
    print(f"Weighted Recall:    {recall:.4f}")
    print(f"Weighted F1-Score:  {f1:.4f}")
    print("\nConfusion Matrix (ALLOW, VERIFY, PAUSE):")
    print(conf_matrix)
    print("\nDetailed Classification Report:")
    print(report_str)
    print("==================================================")

    return {
        "total_samples": len(df),
        "precision": round(float(precision), 4),
        "recall": round(float(recall), 4),
        "f1_score": round(float(f1), 4),
        "confusion_matrix": conf_matrix.tolist()
    }


if __name__ == "__main__":
    evaluate_bharosa_model()
