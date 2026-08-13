import React from 'react';
import { AnalysisRequest, AnalysisResponse } from '../types/risk';
import { NotificationBanner } from './NotificationBanner';
import { RiskResultOverlay } from './RiskResultOverlay';
import { Shield, ArrowRight, CheckCircle2, AlertTriangle, Loader2 } from 'lucide-react';

interface SimulatedUpiAppProps {
  request: AnalysisRequest;
  analysisResult: AnalysisResponse | null;
  isLoading: boolean;
  errorMessage: string | null;
  isOffline: boolean;
  onAnalyze: () => void;
  onPaymentSuccess: () => void;
  onReset: () => void;
}

export const SimulatedUpiApp: React.FC<SimulatedUpiAppProps> = ({
  request,
  analysisResult,
  isLoading,
  errorMessage,
  isOffline,
  onAnalyze,
  onPaymentSuccess,
  onReset,
}) => {
  return (
    <div className="phone-wrapper">
      <div className="phone-frame">
        {/* Top Phone Notch */}
        <div className="phone-notch">
          <div className="phone-speaker"></div>
        </div>

        {/* Screen Content */}
        <div className="phone-screen">
          {/* Simulated UPI Header */}
          <div className="upi-header">
            <div className="upi-logo-badge">
              <Shield size={18} color="#818cf8" />
              <span>BHAROSA PAY</span>
            </div>
            <span style={{ fontSize: '0.65rem', background: 'rgba(99, 102, 241, 0.2)', padding: '0.15rem 0.45rem', borderRadius: '4px', color: '#818cf8', fontWeight: 600 }}>
              BHAROSA ACTIVE
            </span>
          </div>

          {/* Incoming Message Notification Banner */}
          <NotificationBanner message={request.message} />

          {/* Payment Details Card */}
          <div style={{
            background: 'rgba(255, 255, 255, 0.04)',
            border: '1px solid rgba(255, 255, 255, 0.08)',
            borderRadius: '16px',
            padding: '1.1rem',
            textAlign: 'center',
            marginBottom: '1rem'
          }}>
            <div style={{
              width: '48px',
              height: '48px',
              borderRadius: '50%',
              background: 'linear-gradient(135deg, #6366f1 0%, #4f46e5 100%)',
              color: '#ffffff',
              fontWeight: 700,
              fontSize: '1.2rem',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              margin: '0 auto 0.65rem auto',
              boxShadow: '0 4px 10px rgba(99, 102, 241, 0.3)'
            }}>
              {request.recipient.name.charAt(0)}
            </div>

            <h3 style={{ fontSize: '1.05rem', fontWeight: 700, color: '#ffffff', margin: 0 }}>
              {request.recipient.name}
            </h3>
            <p style={{ fontSize: '0.78rem', color: '#818cf8', margin: '0.1rem 0 0.85rem 0', fontFamily: 'monospace' }}>
              {request.recipient.upi_id}
            </p>

            <div style={{
              background: 'rgba(15, 23, 42, 0.6)',
              borderRadius: '12px',
              padding: '0.85rem',
              border: '1px solid rgba(255, 255, 255, 0.05)',
              margin: '0.5rem 0'
            }}>
              <span style={{ fontSize: '0.7rem', color: '#9ca3af', textTransform: 'uppercase', letterSpacing: '0.05em' }}>
                PAYMENT AMOUNT
              </span>
              <div style={{ fontSize: '1.8rem', fontWeight: 800, color: '#ffffff', marginTop: '0.1rem' }}>
                ₹{request.transaction.amount.toLocaleString('en-IN')}
              </div>
              {request.transaction.purpose && (
                <span style={{ fontSize: '0.75rem', color: '#94a3b8', display: 'inline-block', marginTop: '0.2rem' }}>
                  Purpose: <strong>{request.transaction.purpose}</strong>
                </span>
              )}
            </div>
          </div>

          {/* API Error / Offline State Warning (Do not claim SAFE on error!) */}
          {errorMessage && (
            <div style={{
              background: 'rgba(245, 158, 11, 0.15)',
              border: '1px solid rgba(245, 158, 11, 0.4)',
              borderRadius: '12px',
              padding: '0.85rem',
              marginBottom: '1rem',
              display: 'flex',
              alignItems: 'flex-start',
              gap: '0.5rem'
            }}>
              <AlertTriangle size={20} color="#f59e0b" style={{ flexShrink: 0, marginTop: '0.1rem' }} />
              <div>
                <strong style={{ fontSize: '0.82rem', color: '#f59e0b', display: 'block' }}>
                  Backend Verification Alert
                </strong>
                <p style={{ fontSize: '0.78rem', color: '#d1d5db', margin: '0.15rem 0 0 0' }}>
                  {errorMessage}
                </p>
              </div>
            </div>
          )}

          {/* Main Action Area */}
          {!analysisResult && !isLoading && (
            <button
              onClick={onAnalyze}
              className="btn btn-primary"
              style={{
                width: '100%',
                padding: '0.85rem',
                fontSize: '1rem',
                borderRadius: '14px',
                marginTop: 'auto'
              }}
            >
              PAY ₹{request.transaction.amount.toLocaleString('en-IN')} <ArrowRight size={18} />
            </button>
          )}

          {/* Loading Spinner */}
          {isLoading && (
            <div style={{
              background: 'rgba(15, 23, 42, 0.9)',
              borderRadius: '16px',
              padding: '1.5rem',
              textAlign: 'center',
              margin: 'auto 0 0 0'
            }}>
              <Loader2 size={32} color="#818cf8" className="spinner" style={{ margin: '0 auto 0.5rem auto' }} />
              <strong style={{ fontSize: '0.9rem', color: '#ffffff', display: 'block' }}>
                Bharosa Risk Engine Analyzing...
              </strong>
              <span style={{ fontSize: '0.75rem', color: '#9ca3af' }}>
                Evaluating intent, message context, and payment escalation
              </span>
            </div>
          )}

          {/* Risk Result Safety Popup Overlay */}
          {analysisResult && (
            <RiskResultOverlay
              result={analysisResult}
              onDismiss={onReset}
              onProceed={onPaymentSuccess}
            />
          )}
        </div>
      </div>
    </div>
  );
};
