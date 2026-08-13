import React from 'react';
import { ArrowDownLeft, ArrowUpRight, AlertOctagon } from 'lucide-react';

interface IntentMismatchVisualProps {
  believedAction: string;
  actualAction: string;
  amount: number;
}

export const IntentMismatchVisual: React.FC<IntentMismatchVisualProps> = ({
  believedAction,
  actualAction,
  amount,
}) => {
  return (
    <div style={{
      background: 'rgba(244, 63, 94, 0.08)',
      border: '1px dashed rgba(244, 63, 94, 0.4)',
      borderRadius: '12px',
      padding: '0.85rem',
      margin: '0.75rem 0'
    }}>
      <div style={{ display: 'flex', alignItems: 'center', gap: '0.4rem', marginBottom: '0.6rem' }}>
        <AlertOctagon size={16} color="#f43f5e" />
        <span style={{ fontSize: '0.8rem', fontWeight: 700, color: '#f43f5e', textTransform: 'uppercase' }}>
          Context Mismatch Detected
        </span>
      </div>

      <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '0.6rem' }}>
        <div style={{
          background: 'rgba(16, 185, 129, 0.1)',
          border: '1px solid rgba(16, 185, 129, 0.3)',
          borderRadius: '8px',
          padding: '0.5rem 0.65rem',
          textAlign: 'center'
        }}>
          <span style={{ fontSize: '0.68rem', color: '#9ca3af', display: 'block' }}>USER BELIEVES</span>
          <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'center', gap: '0.3rem', marginTop: '0.2rem' }}>
            <ArrowDownLeft size={16} color="#10b981" />
            <strong style={{ color: '#10b981', fontSize: '0.85rem' }}>{believedAction} ₹{amount.toLocaleString('en-IN')}</strong>
          </div>
        </div>

        <div style={{
          background: 'rgba(225, 29, 72, 0.15)',
          border: '1px solid rgba(225, 29, 72, 0.4)',
          borderRadius: '8px',
          padding: '0.5rem 0.65rem',
          textAlign: 'center'
        }}>
          <span style={{ fontSize: '0.68rem', color: '#9ca3af', display: 'block' }}>ACTUAL TRANSACTION</span>
          <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'center', gap: '0.3rem', marginTop: '0.2rem' }}>
            <ArrowUpRight size={16} color="#f43f5e" />
            <strong style={{ color: '#f43f5e', fontSize: '0.85rem' }}>{actualAction} ₹{amount.toLocaleString('en-IN')}</strong>
          </div>
        </div>
      </div>
    </div>
  );
};
