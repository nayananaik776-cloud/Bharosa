# Bharosa API Contract

## Overview
This document defines the API contract between the Bharosa Backend (`backend/`) and Frontend (`frontend/`), Android (`android/`), and ML/NLP (`ml/`) modules.

---

## Endpoint: Analyze Full Payment Context

`POST /api/analyze/full`

### Request Body
```json
{
  "message": "Your ₹5,000 refund has been approved. Scan this QR to receive your refund.",
  "recipient": {
    "name": "Customer Support Ltd",
    "upi_id": "refunds@upi"
  },
  "transaction": {
    "amount": 5000,
    "type": "SEND",
    "purpose": "Refund Processing"
  },
  "intent": {
    "believed_action": "RECEIVE",
    "promised_benefit": "Refund",
    "promised_amount": 5000
  },
  "payment_history": [
    { "amount": 1000, "purpose": "Registration" },
    { "amount": 2000, "purpose": "Verification" }
  ]
}
```

### Response Body (200 OK)
```json
{
  "risk_level": "HIGH",
  "action": "PAUSE",
  "explanation": "STOP — You are about to SEND ₹5,000, not RECEIVE ₹5,000.",
  "signals": [
    {
      "code": "INTENT_MISMATCH",
      "severity": "HIGH",
      "description": "User believes they are receiving funds, but the transaction sends funds."
    }
  ],
  "details": {
    "believed_action": "RECEIVE",
    "actual_action": "SEND",
    "requested_amount": 5000,
    "promised_amount": 5000,
    "advance_payment_detected": false,
    "escalation_detected": false
  },
  "voice_warning": "Warning: You are about to send money instead of receiving money. Please stop and verify."
}
```

### Error Responses
- **400 Bad Request**: Invalid JSON structure or missing required fields.
- **500 Internal Server Error**: Risk engine error.
- **Backend Unavailable / Network Error**: Frontend client handles timeout or connection errors gracefully without defaulting to "SAFE".
