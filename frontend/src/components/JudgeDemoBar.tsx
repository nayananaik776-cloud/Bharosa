import React from 'react';
import { DemoScenario } from '../types/risk';
import { PlayCircle, AlertTriangle, ArrowUpRight, Repeat, ShieldCheck } from 'lucide-react';

interface JudgeDemoBarProps {
  scenarios: DemoScenario[];
  activeScenarioId: string | null;
  onSelectScenario: (scenario: DemoScenario) => void;
}

export const JudgeDemoBar: React.FC<JudgeDemoBarProps> = ({
  scenarios,
  activeScenarioId,
  onSelectScenario,
}) => {
  const getIcon = (id: string) => {
    switch (id) {
      case 'demo-1': return <ArrowUpRight size={18} color="#f43f5e" />;
      case 'demo-2': return <AlertTriangle size={18} color="#f59e0b" />;
      case 'demo-3': return <Repeat size={18} color="#e11d48" />;
      case 'demo-4': return <ShieldCheck size={18} color="#10b981" />;
      default: return <PlayCircle size={18} color="#6366f1" />;
    }
  };

  return (
    <div className="glass-card" style={{ padding: '1.25rem', marginBottom: '1.5rem', borderLeft: '4px solid #6366f1' }}>
      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', flexWrap: 'wrap', gap: '0.5rem', marginBottom: '0.85rem' }}>

        <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
          <PlayCircle size={22} color="#818cf8" />
          <h2 style={{ fontSize: '1.15rem', fontWeight: 700, margin: 0, color: '#ffffff' }}>
            JUDGE DEMO CONTROL — TRY DEMO SCENARIOS
          </h2>
        </div>
        <span style={{ fontSize: '0.85rem', color: '#9ca3af' }}>
          Click any scenario to simulate payment context instantly
        </span>
      </div>

      <div className="demo-scenarios-grid" style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))', gap: '0.85rem' }}>

        {scenarios.map((scenario) => {
          const isActive = activeScenarioId === scenario.id;
          return (
            <button
              key={scenario.id}
              onClick={() => onSelectScenario(scenario)}
              className={`glass-card glass-card-interactive`}
              style={{
                padding: '0.85rem 1rem',
                textAlign: 'left',
                cursor: 'pointer',
                border: isActive ? '2px solid #6366f1' : '1px solid var(--border-glass)',
                background: isActive ? 'rgba(99, 102, 241, 0.15)' : 'rgba(255, 255, 255, 0.03)',
                boxShadow: isActive ? '0 0 15px rgba(99, 102, 241, 0.3)' : 'none',
                display: 'flex',
                flexDirection: 'column',
                gap: '0.4rem',
                outline: 'none'
              }}
            >
              <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', width: '100%' }}>
                <div style={{ display: 'flex', alignItems: 'center', gap: '0.4rem' }}>
                  {getIcon(scenario.id)}
                  <span style={{ fontWeight: 700, fontSize: '0.9rem', color: '#ffffff' }}>
                    {scenario.title}
                  </span>
                </div>
                <span className={`badge ${
                  scenario.badge === 'CRITICAL' ? 'badge-critical' :
                  scenario.badge === 'HIGH' ? 'badge-high' : 'badge-low'
                }`} style={{ fontSize: '0.65rem' }}>
                  {scenario.badge}
                </span>
              </div>
              <p style={{ fontSize: '0.78rem', color: '#9ca3af', margin: 0, lineHeight: 1.3 }}>
                {scenario.subtitle}
              </p>
            </button>
          );
        })}
      </div>
    </div>
  );
};
