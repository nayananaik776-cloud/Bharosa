from fastapi import FastAPI, HTTPException
from fastapi.middleware.cors import CORSMiddleware
from backend.models import AnalysisRequest, AnalysisResponse
from backend.risk_engine import evaluate_risk

app = FastAPI(
    title="Bharosa Fraud Guardian API",
    description="Real-Time Payment Safety & Contextual Fraud Explainer Backend",
    version="1.0.0"
)

# Enable CORS for frontend integration
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

@app.get("/")
def health_check():
    return {"status": "ok", "service": "Bharosa Fraud Guardian API"}

@app.post("/api/analyze/full", response_model=AnalysisResponse)
def analyze_full_payment(request: AnalysisRequest):
    try:
        response = evaluate_risk(request)
        return response
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))
