# Bharosa — Payment Safety Guardian

**TeamVX** | Build Bank FinTech Hackathon (Track 2 — Fraud Detection & Financial Crime Prevention, Problem #4: Real-Time Fraud Explainer)

---

## 🚀 Live Deployments

- 🌐 **Frontend Application (Vercel)**: [https://bharosa-zeta.vercel.app/](https://bharosa-zeta.vercel.app/)
- ⚙️ **Backend Risk Engine API (Render)**: [https://bharosa-k0mp.onrender.com](https://bharosa-k0mp.onrender.com)
- 📖 **API Interactive Documentation (Swagger UI)**: [https://bharosa-k0mp.onrender.com/docs](https://bharosa-k0mp.onrender.com/docs)

---

## 💡 Overview

**Bharosa** is a proactive payment-safety system designed especially for digitally vulnerable users (rural users, first-time digital-payment users, elderly, and users with limited digital/financial literacy). 

Rather than analyzing only transaction amounts or static blocklists, Bharosa analyzes the **story and context** behind every transaction. It reconciles incoming SMS/notification context, user belief, transaction direction, advance-payment promises, and historical payment escalation patterns to intercept financial fraud *before* money leaves the user's account.

---

## ✨ Key Features

- 📱 **Responsive Simulated UPI App (`Bharosa Pay`)**: Mobile and tablet friendly phone simulator replicating real-world UPI user experiences.
- 🎯 **Context Analysis Engine**:
  1. **Intent Mismatch Detection**: Detects QR/Refund scams (e.g. user expects to RECEIVE money, but transaction actually SENDS money).
  2. **Advance-Fee / Pension Scam Detection**: Flags upfront fee requests required to unlock promised benefits/pension payouts.
  3. **Payment Escalation Tracking**: Identifies sequential increasing payments requested for the same promised benefit.
- 🎙️ **Voice Warning Alerts (TTS)**: Delivers clear, plain-language spoken audio warnings tailored for low-literacy or first-time digital users.
- ⚖️ **Judge Demo Control Panel**: Quick 1-click scenario simulation for judge evaluations (Refund QR Scam, Pension Scam, Escalating Fees, Safe Payment).
- 🔍 **Live API Inspector**: Real-time request and response JSON payload viewer for `POST /api/analyze/full`.
- 🔌 **Offline Fallback Engine**: Seamless rule engine fallback ensuring 100% demo availability even if live network or backend services are unreachable.

---

## 🛠️ Repository Structure & Architecture

```
bharosa-fraud-guardian/
├── backend/    # FastAPI Backend + Risk Orchestration Engine
├── android/    # Android Guardian Listener & Accessibility UI
├── frontend/   # React + Vite Simulated Payment Demo & Judge Interface
├── ml/         # ML/NLP Scam Detection & Intent Engines
├── docs/       # Architecture Specs & API Contracts
└── scripts/    # Deployment & utility scripts
```

---

## 🚀 Getting Started (Frontend Local Setup)

1. **Navigate to the frontend directory**:
   ```bash
   cd frontend
   ```

2. **Install dependencies**:
   ```bash
   npm install
   ```

3. **Configure environment variables** (`.env`):
   ```env
   VITE_API_BASE_URL=https://bharosa-k0mp.onrender.com
   ```

4. **Start local development server**:
   ```bash
   npm run dev
   ```

5. **Build for production**:
   ```bash
   npm run build
   ```

- See `frontend/README.md` for details on running the simulated payment demo and judge interface.
- See `android/` and `docs/api-contract.md` for Android Guardian listener and API details.

