from pydantic import BaseModel, Field, model_validator
from typing import List, Optional, Dict, Any, Union

# =======================
# Request Schemas
# =======================

class TransactionSchema(BaseModel):
    amount: float
    currency: str = "INR"
    direction: str = "SEND"
    type: Optional[str] = None
    recipient_id: Optional[str] = None
    recipient_name: Optional[str] = None
    purpose: Optional[str] = None
    is_new_recipient: bool = False

    @model_validator(mode="before")
    @classmethod
    def normalize_fields(cls, values: Any) -> Any:
        if isinstance(values, dict):
            if "type" in values and "direction" not in values:
                values["direction"] = values["type"]
            if "recipient" in values and isinstance(values["recipient"], dict):
                values["recipient_name"] = values["recipient"].get("name")
                values["recipient_id"] = values["recipient"].get("upi_id")
        return values

class UserContextSchema(BaseModel):
    language: str = "en"
    previous_related_payments: List[Dict[str, Any]] = []

class AnalyzeFullRequest(BaseModel):
    message_text: Optional[str] = None
    message: Optional[str] = None
    recipient: Optional[Dict[str, Any]] = None
    transaction: TransactionSchema
    intent: Optional[Dict[str, Any]] = None
    payment_history: Optional[List[Dict[str, Any]]] = None
    user_context: Optional[UserContextSchema] = None

    @model_validator(mode="after")
    def sync_context(self) -> 'AnalyzeFullRequest':
        if not self.message_text and self.message:
            self.message_text = self.message
        elif not self.message and self.message_text:
            self.message = self.message_text

        if self.user_context is None:
            history = self.payment_history or []
            self.user_context = UserContextSchema(previous_related_payments=history)
        elif self.payment_history and not self.user_context.previous_related_payments:
            self.user_context.previous_related_payments = self.payment_history
        return self

class AnalyzeMessageRequest(BaseModel):
    message_text: str

class AnalyzeTransactionRequest(BaseModel):
    transaction: TransactionSchema

class RecipientCheckRequest(BaseModel):
    recipient_id: str

class PaymentSimulateRequest(BaseModel):
    transaction_id: str
    action: str = Field(default="ALLOW", description="ALLOW, PAUSE")

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
    action: Optional[str] = None
    intent_analysis: IntentAnalysis = Field(default_factory=IntentAnalysis)
    promise_analysis: PromiseAnalysis = Field(default_factory=PromiseAnalysis)
    signals: Union[List[str], List[Dict[str, Any]]] = []
    explanation: str
    recommended_action: Optional[str] = "Verify payment details."
    voice_text: Optional[str] = None
    voice_warning: Optional[str] = None

    @model_validator(mode="after")
    def populate_aliases(self) -> 'AnalyzeFullResponse':
        if not self.action:
            self.action = self.decision
        if not self.voice_warning and self.voice_text:
            self.voice_warning = self.voice_text
        elif not self.voice_text and self.voice_warning:
            self.voice_text = self.voice_warning
        return self

