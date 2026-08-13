from typing import List, Optional
from pydantic import BaseModel, Field

class Recipient(BaseModel):
    name: str
    upi_id: str

class Transaction(BaseModel):
    amount: float
    type: str = Field(..., description="SEND or RECEIVE")
    purpose: Optional[str] = None

class Intent(BaseModel):
    believed_action: Optional[str] = Field(None, description="SEND or RECEIVE")
    promised_benefit: Optional[str] = None
    promised_amount: Optional[float] = None

class PaymentHistoryItem(BaseModel):
    amount: float
    purpose: Optional[str] = None

class AnalysisRequest(BaseModel):
    message: str
    recipient: Recipient
    transaction: Transaction
    intent: Optional[Intent] = None
    payment_history: Optional[List[PaymentHistoryItem]] = Field(default_factory=list)

class Signal(BaseModel):
    code: str
    severity: str
    description: str

class AnalysisDetails(BaseModel):
    believed_action: str
    actual_action: str
    requested_amount: float
    promised_amount: float
    advance_payment_detected: bool
    escalation_detected: bool

class AnalysisResponse(BaseModel):
    risk_level: str  # LOW, MEDIUM, HIGH, CRITICAL
    action: str      # ALLOW, VERIFY, PAUSE
    explanation: str
    signals: List[Signal]
    details: AnalysisDetails
    voice_warning: Optional[str] = None
