# Bharosa API Contract

This document specifies the REST API contract between client applications (including the Android Guardian app and web frontend) and the Bharosa Fraud Analysis Backend.

---

## Base URL Configuration

- **Default Backend URL**: `http://localhost:8000`
- **Android Emulator Backend URL**: `http://10.0.2.2:8000`
- **Configurability**: Must be user-configurable via app settings or environment variables. Never hardcode credentials or backend secrets.

---

## Endpoints

### 1. Analyze Message / Notification

Analyzes incoming financial notification or message text to detect potential scam patterns, calculate risk scores, and generate clear, non-technical warnings in requested languages.

- **URL**: `/api/v1/analyze`
- **Method**: `POST`
- **Headers**:
  - `Content-Type: application/json`

#### Request Payload Schema

```json
{
  "text": "Pay ₹2,000 verification fee to unlock your ₹50,000 pension refund",
  "sender": "WhatsApp",
  "package_name": "com.whatsapp",
  "timestamp": "2026-08-11T20:30:00Z",
  "language": "hi"
}
```

| Field | Type | Description |
|-------|------|-------------|
| `text` | `string` | The text extracted from notification title + content. |
| `sender` | `string` | App or sender name (e.g. WhatsApp, HDFC, SMS). |
| `package_name` | `string` | Android package name of originating app (optional). |
| `timestamp` | `string` | ISO 8601 UTC timestamp when notification arrived. |
| `language` | `string` | Preferred language code for warnings (`en` or `hi`). |

---

#### Response Payload Schema (200 OK)

```json
{
  "risk_score": 0.92,
  "risk_level": "HIGH",
  "category": "ADVANCE_FEE_SCAM",
  "is_scam": true,
  "warning_title": {
    "en": "BE CAREFUL",
    "hi": "सावधान रहें"
  },
  "warning_message": {
    "en": "You are being asked to send ₹2,000 first for a promised ₹50,000 benefit.",
    "hi": "आपको वादा किए गए ₹50,000 के लाभ के लिए पहले ₹2,000 भेजने के लिए कहा जा रहा है।"
  },
  "action_required": "DO_NOT_PAY",
  "explanation_audio_text": {
    "en": "Warning! You are being asked to send 2,000 rupees as an advance verification fee. Legitimate offers never ask for advance payments. Do not transfer any money.",
    "hi": "सावधान! आपसे अग्रिम सत्यापन शुल्क के रूप में 2,000 रुपये भेजने के लिए कहा जा रहा है। वैध ऑफर कभी भी अग्रिम भुगतान नहीं मांगते। कोई भी पैसा ट्रांसफर न करें।"
  }
}
```

| Field | Type | Description |
|-------|------|-------------|
| `risk_score` | `number` | Floating point score between `0.0` (safe) and `1.0` (high risk). |
| `risk_level` | `string` | `HIGH`, `MEDIUM`, `LOW`, or `SAFE`. |
| `category` | `string` | Scam classification (e.g. `ADVANCE_FEE_SCAM`, `REFUND_SCAM`, `ACCOUNT_BLOCK_SCAM`, `QR_SCAM`, `SAFE_PAYMENT`). |
| `is_scam` | `boolean` | `true` if risk requires user alert. |
| `warning_title` | `object` | Dictionary containing simple localized title string (`en`, `hi`). |
| `warning_message` | `object` | Non-technical explanation of the risk (`en`, `hi`). |
| `action_required` | `string` | Recommended user action (`DO_NOT_PAY`, `DO_NOT_CLICK`, `VERIFY_FIRST`, `SAFE`). |
| `explanation_audio_text` | `object` | Text optimized for Text-To-Speech (TTS) voice engines. |

---

## Offline & Error Fallback Contract

If the client experiences network timeout, offline status, or a 50x backend error, the client **MUST NOT** declare the message safe.

Instead, the client must apply the fallback risk assessment:

```json
{
  "risk_score": 0.5,
  "risk_level": "UNKNOWN",
  "category": "UNVERIFIED_FINANCIAL",
  "is_scam": false,
  "warning_title": {
    "en": "VERIFY BEFORE PAYING",
    "hi": "भुगतान करने से पहले जांच करें"
  },
  "warning_message": {
    "en": "Unable to verify right now. Please verify before paying.",
    "hi": "अभी पुष्टि करने में असमर्थ। कृपया भुगतान करने से पहले जांच लें।"
  },
  "action_required": "VERIFY_FIRST",
  "explanation_audio_text": {
    "en": "Unable to verify message right now. Please double check with your bank before sending any money.",
    "hi": "अभी संदेश की पुष्टि करने में असमर्थ। कृपया कोई भी पैसा भेजने से पहले अपने बैंक से जांच करें।"
  }
}
```
