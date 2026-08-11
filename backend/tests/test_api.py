import pytest
from fastapi.testclient import TestClient
from backend.main import app

client = TestClient(app)

def test_health_check():
    response = client.get("/api/health")
    assert response.status_code == 200
    assert response.json() == {"status": "healthy"}

def test_safe_payment():
    payload = {
        "message_text": "Paying for groceries",
        "transaction": {
            "amount": 500,
            "currency": "INR",
            "direction": "SEND",
            "recipient_id": "store_123",
            "recipient_name": "Local Store",
            "is_new_recipient": False
        },
        "user_context": {
            "language": "en",
            "previous_related_payments": []
        }
    }
    response = client.post("/api/analyze/full", json=payload)
    assert response.status_code == 200
    data = response.json()
    assert data["risk_level"] == "LOW"
    assert data["decision"] == "ALLOW"

def test_advance_fee_scam():
    payload = {
        "message_text": "Your ₹50,000 pension has been approved. Pay ₹2,000 verification fee to receive it.",
        "transaction": {
            "amount": 2000,
            "currency": "INR",
            "direction": "SEND",
            "recipient_id": "scammer_1",
            "recipient_name": "Fake Services",
            "is_new_recipient": True
        },
        "user_context": {
            "language": "en",
            "previous_related_payments": []
        }
    }
    response = client.post("/api/analyze/full", json=payload)
    assert response.status_code == 200
    data = response.json()
    assert data["risk_level"] == "HIGH"
    assert data["decision"] == "PAUSE"
    assert "ADVANCE_FEE_REQUEST" in data["signals"]

def test_refund_qr_scam():
    payload = {
        "message_text": "Scan this QR to receive your refund of ₹5,000.",
        "transaction": {
            "amount": 5000,
            "currency": "INR",
            "direction": "SEND",
            "recipient_id": "scammer_2",
            "recipient_name": "Fake Refund Agent",
            "is_new_recipient": True
        },
        "user_context": {
            "language": "en",
            "previous_related_payments": []
        }
    }
    response = client.post("/api/analyze/full", json=payload)
    assert response.status_code == 200
    data = response.json()
    assert data["risk_level"] == "CRITICAL"
    assert data["decision"] == "PAUSE"
    assert "INTENT_MISMATCH" in data["signals"]

def test_escalation_scam():
    payload = {
        "message_text": "Pay 5000 GST to release the fund.",
        "transaction": {
            "amount": 5000,
            "currency": "INR",
            "direction": "SEND",
            "recipient_id": "scammer_3",
            "recipient_name": "Fake Agent",
            "is_new_recipient": False
        },
        "user_context": {
            "language": "en",
            "previous_related_payments": [{"amount": 1000}, {"amount": 2000}]
        }
    }
    response = client.post("/api/analyze/full", json=payload)
    assert response.status_code == 200
    data = response.json()
    assert data["risk_level"] == "CRITICAL"
    assert data["decision"] == "PAUSE"
    assert "PAYMENT_ESCALATION" in data["signals"]
