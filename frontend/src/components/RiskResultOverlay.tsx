import React from 'react';
import { AnalysisResponse, RiskLevel, RiskAction } from '../types/risk';
import { IntentMismatchVisual } from './IntentMismatchVisual';
import { AdvanceFeeVisual } from './AdvanceFeeVisual';
import { EscalationVisual } from './EscalationVisual';
import { VoiceAlert } from './VoiceAlert';
import { ShieldAlert, ShieldCheck, AlertTriangle, CheckCircle2, XCircle } from 'lucide-react';

interface RiskResultOverlayProps {
  result: AnalysisResponse;
  onDismiss: () => void;
  onProceed: () => void;
}

export const RiskResultOverlay: React.FC<RiskResultOverlayProps> = ({
  result,
  onDismiss,
  onProceed,
}) => {
  const getHeaderIcon = (level: RiskLevel) => {
    switch (level) {
      case 'CRITICAL':
      case 'HIGH':
        return <ShieldAlert size={28} color="#f43f5e" />;
      case 'MEDIUM':
        return <AlertTriangle size={28} color="#f59e0b" />;
      case 'LOW':
        return <ShieldCheck size={28} color="#10b981" />;
    }
  };

  const getActionBadgeClass = (action: RiskAction) => {
    switch (action) {
      case 'PAUSE': return 'badge-critical';
      case 'VERIFY': return 'badge-medium';
      case 'ALLOW': return 'badge-low';
    }
  };

  return (
    <div style={{
      background: 'rgba(15, 23, 42, 0.96)',
      backdropFilter: 'blur(20px)',
      border: `2px solid ${result.risk_level === 'CRITICAL' ? '#f43f5e' : result.risk_level === 'HIGH' ? '#f43f5e' : '#10b981'}`,
      borderRadius: '20px',
      padding: '1.25rem',
      marginTop: '1rem',
      boxShadow: result.risk_level === 'CRITICAL' || result.risk_level === 'HIGH' ? 'var(--shadow-alert)' : 'var(--shadow-glow)'
    }}>
      {/* Header */}
      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: '0.85rem' }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: '0.65rem' }}>
          {getHeaderIcon(result.risk_level)}
          <div>
            <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
              <span className={`badge ${
                result.risk_level === 'CRITICAL' ? 'badge-critical' :
                result.risk_level === 'HIGH' ? 'badge-high' : 'badge-low'
              }`}>
                {result.risk_level} RISK
              </span>
              <span className={`badge ${getActionBadgeClass(result.action)}`}>
                {result.action}
              </span>
            </div>
          </div>
        </div>

        <VoiceAlert warningText={result.voice_warning} autoPlay={true} />
      </div>

      {/* Primary Explanation Box */}
      <div style={{
        background: result.risk_level === 'LOW' ? 'rgba(16, 185, 129, 0.1)' : 'rgba(225, 29, 72, 0.12)',
        borderLeft: `4px solid ${result.risk_level === 'LOW' ? '#10b981' : '#f43f5e'}`,
        borderRadius: '8px',
        padding: '0.85rem',
        marginBottom: '0.85rem'
      }}>
        <h3 style={{
          fontSize: '0.95rem',
          fontWeight: 700,
          color: result.risk_level === 'LOW' ? '#10b981' : '#ffffff',
          margin: 0,
          lineHeight: 1.35
        }}>
          {result.explanation}
        </h3>
      </div>

      {/* Signal Specific Visuals */}
      {result.details && (
        <>
          {result.details.believed_action !== result.details.actual_action && (
            <IntentMismatchVisual
              believedAction={result.details.believed_action}
              actualAction={result.details.actual_action}
              amount={result.details.requested_amount}
            />
          )}

          {result.details.advance_payment_detected && (
            <AdvanceFeeVisual
              requestedAmount={result.details.requested_amount}
              promisedAmount={result.details.promised_amount}
            />
          )}

          {result.details.escalation_detected && (
            <EscalationVisual
              history={[
                { amount: 1000, purpose: 'Registration' },
                { amount: 2000, purpose: 'Verification' }
              ]}
              currentAmount={result.details.requested_amount}
            />
          )}
        </>
      )}

      {/* Extracted Signal Badges */}
      {result.signals && result.signals.length > 0 && (
        <div style={{ margin: '0.75rem 0' }}>
          <span style={{ fontSize: '0.7rem', color: '#9ca3af', fontWeight: 600, display: 'block', marginBottom: '0.35rem' }}>
            Risk Signals Extracted:
          </span>
          <div style={{ display: 'flex', flexWrap: 'wrap', gap: '0.4rem' }}>
            {result.signals.map((sig, idx) => (
              <span key={idx} className="badge badge-high" style={{ fontSize: '0.65rem', padding: '0.2rem 0.5rem' }}>
                {sig.code}: {sig.description}
              </span>
            ))}
          </div>
        </div>
      )}

      {/* Action Buttons */}
      <div style={{ display: 'flex', gap: '0.65rem', marginTop: '1rem' }}>
        {result.action === 'PAUSE' || result.risk_level === 'HIGH' || result.risk_level === 'CRITICAL' ? (
          <>
            <button
              onClick={onDismiss}
              className="btn btn-danger"
              style={{ flex: 1, padding: '0.65rem', fontSize: '0.85rem' }}
            >
              <XCircle size={16} /> PAUSE PAYMENT & VERIFY
            </button>
            <button
              onClick={onProceed}
              className="btn btn-outline"
              style={{ padding: '0.65rem', fontSize: '0.75rem', opacity: 0.7 }}
            >
              Proceed Anyway
            </button>
          </>
        ) : (
          <button
            onClick={onProceed}
            className="btn btn-primary"
            style={{ width: '100%', padding: '0.65rem', fontSize: '0.85rem', background: 'linear-gradient(135deg, #10b981 0%, #059669 100%)' }}
          >
            <CheckCircle2 size={16} /> CONFIRM PAYMENT (₹{result.details.requested_amount})
          </button>
        )}
      </div>
    </div>
  );
};
