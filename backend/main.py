from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from .routers import health, analyze, payment, recipient
from .database import engine, Base

# Create database tables
Base.metadata.create_all(bind=engine)

app = FastAPI(
    title="Bharosa Backend",
    description="Risk orchestration API for Bharosa Fraud Guardian",
    version="1.0.0"
)

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

@app.get("/")
def root():
    return {
        "status": "online",
        "service": "Bharosa Risk Engine API",
        "version": "1.0.0",
        "documentation": "/docs",
        "interactive_docs_url": "https://bharosa-k0mp.onrender.com/docs",
        "endpoints": {
            "health": "/api/health",
            "analyze_full": "/api/analyze/full (POST)",
            "simulate_payment": "/api/payment/simulate (POST)",
            "check_recipient": "/api/recipient/check (POST)"
        }
    }

app.include_router(health.router)
app.include_router(analyze.router)
app.include_router(payment.router)
app.include_router(recipient.router)

