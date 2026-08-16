# Bharosa Frontend — Simulated Payment & Judge Demo

This module contains the judge-facing interactive prototype for **Bharosa — Real-Time Payment Context & Fraud Explainer Guardian**.

Built for Track 2 (Fraud Detection & Financial Crime Prevention, Problem #4: Real-Time Fraud Explainer) by **TeamVX**.

---

## 🚀 Live Links

- 🌐 **Live Demo (Vercel)**: [https://bharosa-zeta.vercel.app/](https://bharosa-zeta.vercel.app/)
- ⚙️ **Backend API (Render)**: [https://bharosa-k0mp.onrender.com](https://bharosa-k0mp.onrender.com)
- 📖 **API Docs (Swagger UI)**: [https://bharosa-k0mp.onrender.com/docs](https://bharosa-k0mp.onrender.com/docs)

---

## Key Features

- **Simulated UPI Payment App (`Bharosa Pay`)**: Realistic mobile phone interface rendering incoming messages, recipient details, payment amount, and purpose. Fully mobile and tablet friendly.
- **Judge Demo Control Panel (`TRY DEMO`)**: One-click scenario buttons enabling immediate testing without typing:
  1. **Refund QR Scam**: Intent Mismatch (Claims RECEIVE ₹5,000, transaction SEND ₹5,000).
  2. **Pension Fee Scam**: Advance-Fee Risk (Demands ₹2,000 upfront for promised ₹50,000 pension).
  3. **Payment Escalation**: Recurring fee trap (₹1,000 → ₹2,000 → ₹5,000).
  4. **Safe Payment**: Legitimate grocery purchase (₹450).
- **Context Risk Explainer Overlay**: Displays `LOW`, `MEDIUM`, `HIGH`, `CRITICAL` risk tiers with explicit `ALLOW`, `VERIFY`, or `PAUSE` actions.
- **Visual Signal Indicators**:
  - `IntentMismatchVisual`: Renders user belief vs actual transaction movement.
  - `AdvanceFeeVisual`: Highlights real money leaving vs promised benefit.
  - `EscalationVisual`: Visualizes payment history escalation chain.
- **Voice Warning Alert (`TTS`)**: Automated speech alert delivering plain-language warnings for vulnerable users.
- **Live API Inspector**: Real-time inspection of `POST /api/analyze/full` request and response payloads.
- **API Error & Standalone Offline Fallback**: Prominently warns `"Unable to verify this payment right now. Please verify before continuing."` when live backend API is unreachable, while providing an offline rule engine so judges can test all visual workflows standalone.

---

## Environment Setup

Create a `.env` file inside `frontend/`:

```bash
VITE_API_BASE_URL=https://bharosa-k0mp.onrender.com
```

Refer to `.env.example` for reference.

---

## Running the Application

### 1. Install Dependencies
```bash
npm install
```

### 2. Development Mode
```bash
npm run dev
```

The application will be available at `http://localhost:5173`.

### 3. Production Build
```bash
npm run build
```

---

## Architecture & Integration

- **Frontend Stack**: React 18, Vite, TypeScript, Lucide Icons.
- **Backend API Integration**: Connects via `POST /api/analyze/full`.
- **API Payload Contract**:
  - **Request**: `message`, `recipient` `{name, upi_id}`, `transaction` `{amount, type, purpose}`, `intent` `{believed_action, promised_benefit, promised_amount}`, `payment_history`.
  - **Response**: `risk_level`, `action`, `explanation`, `signals`, `details`, `voice_warning`.

