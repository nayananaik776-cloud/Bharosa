from pydantic import BaseModel, Field
from typing import List, Optional, Dict, Any

# =======================
# Request Schemas
# =======================

class TransactionSchema(BaseModel):
    amount: float
    currency: str = "INR"
    direction: str = Field(description="SEND or RECEIVE")
    recipient_id: str
    recipient_name: str
    is_new_recipient: bool

class UserContextSchema(BaseModel):
    language: str = "en"
    previous_related_payments: List[Dict[str, Any]] = []

class AnalyzeFullRequest(BaseModel):
    message_text: str = ""
    transaction: TransactionSchema
    user_context: UserContextSchema

class AnalyzeMessageRequest(BaseModel):
    message_text: str

class AnalyzeTransactionRequest(BaseModel):
    transaction: TransactionSchema

class RecipientCheckRequest(BaseModel):
    recipient_id: str

class PaymentSimulateRequest(BaseModel):
    transaction_id: str
    action: str = Field(description="ALLOW, PAUSE")

# =======================
# Response Schemas
# =======================

class IntentAnalysis(BaseModel):
    detected_intent: Optional[str] = None
    mismatch: bool = False

class PromiseAnalysis(BaseModel):
    promised_amount: Optional[float] = None
    condition: Optional[str] = None

class AnalyzeFullResponse(BaseModel):
    risk_score: int
    risk_level: str
    decision: str
    intent_analysis: IntentAnalysis = Field(default_factory=IntentAnalysis)
    promise_analysis: PromiseAnalysis = Field(default_factory=PromiseAnalysis)
    signals: List[str] = []
    explanation: str
    recommended_action: str
    voice_text: str
