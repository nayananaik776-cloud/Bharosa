from sqlalchemy import Column, Integer, String, Float, Boolean, JSON, DateTime
from sqlalchemy.sql import func
from .database import Base

class User(Base):
    __tablename__ = "users"
    id = Column(Integer, primary_key=True, index=True)
    device_id = Column(String, unique=True, index=True)
    language = Column(String, default="en")

class Recipient(Base):
    __tablename__ = "recipients"
    id = Column(Integer, primary_key=True, index=True)
    recipient_id = Column(String, unique=True, index=True)
    name = Column(String)
    risk_score = Column(Integer, default=0)
    reports_count = Column(Integer, default=0)

class Transaction(Base):
    __tablename__ = "transactions"
    id = Column(Integer, primary_key=True, index=True)
    user_id = Column(Integer)
    recipient_id = Column(String)
    amount = Column(Float)
    currency = Column(String)
    direction = Column(String)
    status = Column(String)
    created_at = Column(DateTime(timezone=True), server_default=func.now())

class AnalysisEvent(Base):
    __tablename__ = "analysis_events"
    id = Column(Integer, primary_key=True, index=True)
    transaction_id = Column(Integer, nullable=True)
    risk_level = Column(String)
    decision = Column(String)
    signals = Column(JSON)
    created_at = Column(DateTime(timezone=True), server_default=func.now())

class ScamCase(Base):
    __tablename__ = "scam_cases"
    id = Column(Integer, primary_key=True, index=True)
    description = Column(String)
    pattern = Column(JSON)
