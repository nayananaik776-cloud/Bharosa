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

app.include_router(health.router)
app.include_router(analyze.router)
app.include_router(payment.router)
app.include_router(recipient.router)
