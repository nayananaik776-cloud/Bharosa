import React from 'react';
import { ShieldCheck, Cpu, Activity } from 'lucide-react';

interface HeaderProps {
  isBackendConnected: boolean;
  onReset: () => void;
}

export const Header: React.FC<HeaderProps> = ({ isBackendConnected, onReset }) => {
  return (
    <header className="glass-card" style={{ padding: '1rem 1.5rem', marginBottom: '1rem' }}>
      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', flexWrap: 'wrap', gap: '1rem' }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: '0.85rem' }}>
          <div style={{
            background: 'linear-gradient(135deg, #6366f1 0%, #4f46e5 100%)',
            padding: '0.6rem',
            borderRadius: '12px',
            boxShadow: '0 0 15px rgba(99, 102, 241, 0.4)',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center'
          }}>
            <ShieldCheck size={28} color="#ffffff" />
          </div>
          <div>
            <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
              <h1 style={{ fontSize: '1.4rem', fontWeight: 800, color: '#ffffff', margin: 0 }}>BHAROSA</h1>
              <span className="badge badge-low" style={{ fontSize: '0.65rem', padding: '0.15rem 0.5rem' }}>TeamVX</span>
            </div>
            <p style={{ fontSize: '0.85rem', color: '#9ca3af', margin: 0 }}>
              Real-Time Payment Context & Fraud Explainer Guardian
            </p>
          </div>
        </div>

        <div style={{ display: 'flex', alignItems: 'center', gap: '1rem' }}>
          <div style={{
            display: 'flex',
            alignItems: 'center',
            gap: '0.5rem',
            background: 'rgba(255, 255, 255, 0.05)',
            padding: '0.4rem 0.85rem',
            borderRadius: '9999px',
            border: '1px solid rgba(255, 255, 255, 0.08)',
            fontSize: '0.8rem'
          }}>
            <Activity size={14} color={isBackendConnected ? '#10b981' : '#f59e0b'} />
            <span style={{ color: '#d1d5db' }}>
              API: <strong style={{ color: isBackendConnected ? '#10b981' : '#f59e0b' }}>
                {isBackendConnected ? 'Connected' : 'Standalone / Fallback'}
              </strong>
            </span>
          </div>

          <button
            onClick={onReset}
            className="btn btn-outline"
            style={{ padding: '0.4rem 0.85rem', fontSize: '0.8rem' }}
          >
            Reset Simulator
          </button>
        </div>
      </div>
    </header>
  );
};
