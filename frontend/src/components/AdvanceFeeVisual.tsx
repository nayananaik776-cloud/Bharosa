import React from 'react';
import { DollarSign, Gift, ArrowRight } from 'lucide-react';

interface AdvanceFeeVisualProps {
  requestedAmount: number;
  promisedAmount: number;
}

export const AdvanceFeeVisual: React.FC<AdvanceFeeVisualProps> = ({
  requestedAmount,
  promisedAmount,
}) => {
  return (
    <div style={{
      background: 'rgba(245, 158, 11, 0.08)',
      border: '1px dashed rgba(245, 158, 11, 0.4)',
      borderRadius: '12px',
      padding: '0.85rem',
      margin: '0.75rem 0'
    }}>
      <span style={{ fontSize: '0.78rem', fontWeight: 700, color: '#f59e0b', textTransform: 'uppercase', display: 'block', marginBottom: '0.5rem' }}>
        Advance-Fee Scam Signal
      </span>

      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', gap: '0.5rem' }}>
        <div style={{
          background: 'rgba(225, 29, 72, 0.15)',
          border: '1px solid rgba(225, 29, 72, 0.4)',
          borderRadius: '8px',
          padding: '0.55rem',
          flex: 1,
          textAlign: 'center'
        }}>
          <span style={{ fontSize: '0.65rem', color: '#f43f5e', fontWeight: 700, display: 'block' }}>REAL MONEY LEAVES</span>
          <strong style={{ fontSize: '0.95rem', color: '#ffffff', display: 'block', marginTop: '0.15rem' }}>
            ₹{requestedAmount.toLocaleString('en-IN')}
          </strong>
        </div>

        <ArrowRight size={18} color="#f59e0b" />

        <div style={{
          background: 'rgba(99, 102, 241, 0.15)',
          border: '1px solid rgba(99, 102, 241, 0.4)',
          borderRadius: '8px',
          padding: '0.55rem',
          flex: 1,
          textAlign: 'center'
        }}>
          <span style={{ fontSize: '0.65rem', color: '#818cf8', fontWeight: 700, display: 'block' }}>PROMISED BENEFIT</span>
          <strong style={{ fontSize: '0.95rem', color: '#ffffff', display: 'block', marginTop: '0.15rem' }}>
            {promisedAmount > 0 ? `₹${promisedAmount.toLocaleString('en-IN')}` : 'Reward / Pension'}
          </strong>
        </div>
      </div>
    </div>
  );
};
