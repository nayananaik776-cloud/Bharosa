# Bharosa — Payment Safety Guardian

**TeamVX** | Build Bank FinTech Hackathon (Track 2 — Fraud Detection & Financial Crime Prevention, Problem #4: Real-Time Fraud Explainer)

## Overview
Bharosa is a proactive payment-safety system designed especially for digitally vulnerable users (rural users, first-time digital-payment users, elderly, and users with limited digital/financial literacy). Rather than analyzing only transaction amounts, Bharosa analyzes the **story and context** behind every transaction.

## Repository Structure & Architecture
- **backend/**: FastAPI backend, risk orchestration, and API.
- **android/**: Android application acting as the guardian listener and UI.
- **frontend/**: React + Vite simulated payment UI and judge demo.
- **ml/**: ML and NLP models for signal extraction, intent engines, and intelligence.
- **docs/**: Architecture & API Contracts.
- **scripts/**: Utility & deployment scripts.

```
bharosa-fraud-guardian/
├── backend/    # FastAPI Backend + Risk Orchestration (Member 1)
├── android/    # Android Guardian Listener & UI (Member 2)
├── frontend/   # React + Vite Simulated Payment & Judge Demo (Member 3)
├── ml/         # ML/NLP Scam Detection & Intent Engines (Member 4)
├── docs/       # Architecture & API Contracts
└── scripts/    # Utility & deployment scripts
```

## Development & Setup
See module-specific documentation for details on running each part:
- See `frontend/README.md` for details on running the simulated payment demo and judge interface.
- See `android/` and `docs/api-contract.md` for Android Guardian listener and API details.
