import React from 'react';
import { PaymentHistoryItem } from '../types/risk';
import { TrendingUp, AlertTriangle } from 'lucide-react';

interface EscalationVisualProps {
  history: PaymentHistoryItem[];
  currentAmount: number;
}

export const EscalationVisual: React.FC<EscalationVisualProps> = ({
  history,
  currentAmount,
}) => {
  const allPayments = [...history, { amount: currentAmount, purpose: 'Current Request' }];

  return (
    <div style={{
      background: 'rgba(225, 29, 72, 0.1)',
      border: '1px solid rgba(225, 29, 72, 0.4)',
      borderRadius: '12px',
      padding: '0.85rem',
      margin: '0.75rem 0'
    }}>
      <div style={{ display: 'flex', alignItems: 'center', gap: '0.4rem', marginBottom: '0.6rem' }}>
        <TrendingUp size={16} color="#f43f5e" />
        <span style={{ fontSize: '0.8rem', fontWeight: 700, color: '#f43f5e', textTransform: 'uppercase' }}>
          Payment Escalation Pattern Detected
        </span>
      </div>

      <div style={{ display: 'flex', alignItems: 'center', gap: '0.35rem', overflowX: 'auto', paddingBottom: '0.25rem' }}>
        {allPayments.map((item, idx) => {
          const isCurrent = idx === allPayments.length - 1;
          return (
            <React.Fragment key={idx}>
              <div style={{
                background: isCurrent ? 'rgba(225, 29, 72, 0.25)' : 'rgba(255, 255, 255, 0.05)',
                border: isCurrent ? '1px solid #f43f5e' : '1px solid rgba(255, 255, 255, 0.1)',
                borderRadius: '8px',
                padding: '0.4rem 0.6rem',
                minWidth: '75px',
                textAlign: 'center'
              }}>
                <span style={{ fontSize: '0.6rem', color: '#9ca3af', display: 'block' }}>
                  {item.purpose || `Payment #${idx + 1}`}
                </span>
                <strong style={{ fontSize: '0.85rem', color: isCurrent ? '#f43f5e' : '#e2e8f0' }}>
                  ₹{item.amount.toLocaleString('en-IN')}
                </strong>
              </div>
              {idx < allPayments.length - 1 && (
                <span style={{ color: '#f43f5e', fontWeight: 700, fontSize: '0.8rem' }}>→</span>
              )}
            </React.Fragment>
          );
        })}
      </div>
    </div>
  );
};
