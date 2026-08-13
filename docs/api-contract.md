# Bharosa API Contract

This document defines the API contract between the Bharosa backend and the various clients (Android Guardian, Frontend).

## 1. Health Check
`GET /api/health`
**Description**: Check if the backend is running.
**Response**:
```json
{
  "status": "healthy"
}
```

## 2. Analyze Full Context
`POST /api/analyze/full`
**Description**: Core endpoint to evaluate risk based on message text, transaction details, and user context.
**Request**:
```json
{
  "message_text": "Your ₹50,000 pension has been approved. Pay ₹2,000 verification fee to receive it.",
  "transaction": {
    "amount": 2000,
    "currency": "INR",
    "direction": "SEND",
    "recipient_id": "demo_recipient_001",
    "recipient_name": "Example Services",
    "is_new_recipient": true
  },
  "user_context": {
    "language": "hi",
    "previous_related_payments": []
  }
}
```
**Response**:
```json
{
  "risk_score": 85,
  "risk_level": "HIGH",
  "decision": "PAUSE",
  "intent_analysis": {
    "detected_intent": "RECEIVE",
    "mismatch": true
  },
  "promise_analysis": {
    "promised_amount": 50000,
    "condition": "PAYMENT_FIRST"
  },
  "signals": ["ADVANCE_FEE_REQUEST", "INTENT_MISMATCH"],
  "explanation": "Be careful. You are being asked to pay ₹2,000 first for a promised ₹50,000 benefit.",
  "recommended_action": "Verify the recipient before proceeding.",
  "voice_text": "Be careful. You are being asked to pay 2000 rupees first for a promised 50000 rupees benefit."
}
```

## 3. Analyze Message (Partial)
`POST /api/analyze/message`
**Description**: Analyzes only the message content.

## 4. Analyze Transaction (Partial)
`POST /api/analyze/transaction`
**Description**: Analyzes only the transaction context.

## 5. Recipient Check
`POST /api/recipient/check`
**Description**: Check historical risk of a recipient.

## 6. Payment Simulation
`POST /api/payment/simulate`
**Description**: Simulate the execution of a payment after analysis.
