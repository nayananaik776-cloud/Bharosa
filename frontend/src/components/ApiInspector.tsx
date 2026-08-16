import React, { useState } from 'react';
import { AnalysisRequest, AnalysisResponse } from '../types/risk';
import { Code, Copy, Check } from 'lucide-react';

interface ApiInspectorProps {
  request: AnalysisRequest;
  response: AnalysisResponse | null;
}

export const ApiInspector: React.FC<ApiInspectorProps> = ({ request, response }) => {
  const [copied, setCopied] = useState(false);
  const [activeTab, setActiveTab] = useState<'request' | 'response'>('response');

  const payloadText = activeTab === 'request'
    ? JSON.stringify(request, null, 2)
    : response ? JSON.stringify(response, null, 2) : '// No response generated yet. Trigger a payment analysis.';

  const handleCopy = () => {
    navigator.clipboard.writeText(payloadText);
    setCopied(true);
    setTimeout(() => setCopied(false), 2000);
  };

  return (
    <div className="glass-card" style={{ padding: '1.25rem', marginTop: '1.5rem' }}>
      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', flexWrap: 'wrap', gap: '0.5rem', marginBottom: '0.85rem' }}>

        <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
          <Code size={20} color="#818cf8" />
          <h3 style={{ fontSize: '1rem', fontWeight: 700, margin: 0, color: '#ffffff' }}>
            LIVE API PAYLOAD INSPECTOR (POST /api/analyze/full)
          </h3>
        </div>

        <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
          <button
            onClick={() => setActiveTab('response')}
            className="btn btn-outline"
            style={{
              padding: '0.3rem 0.65rem',
              fontSize: '0.75rem',
              borderColor: activeTab === 'response' ? '#6366f1' : 'rgba(255,255,255,0.1)',
              background: activeTab === 'response' ? 'rgba(99, 102, 241, 0.2)' : 'transparent',
              color: activeTab === 'response' ? '#818cf8' : '#9ca3af'
            }}
          >
            Response JSON
          </button>
          <button
            onClick={() => setActiveTab('request')}
            className="btn btn-outline"
            style={{
              padding: '0.3rem 0.65rem',
              fontSize: '0.75rem',
              borderColor: activeTab === 'request' ? '#6366f1' : 'rgba(255,255,255,0.1)',
              background: activeTab === 'request' ? 'rgba(99, 102, 241, 0.2)' : 'transparent',
              color: activeTab === 'request' ? '#818cf8' : '#9ca3af'
            }}
          >
            Request JSON
          </button>
          <button
            onClick={handleCopy}
            className="btn btn-outline"
            style={{ padding: '0.3rem 0.65rem', fontSize: '0.75rem' }}
          >
            {copied ? <Check size={14} color="#10b981" /> : <Copy size={14} />}
            {copied ? 'Copied' : 'Copy'}
          </button>
        </div>
      </div>

      <pre style={{
        background: '#090d16',
        border: '1px solid rgba(255, 255, 255, 0.08)',
        borderRadius: '10px',
        padding: '1rem',
        fontSize: '0.8rem',
        color: '#38bdf8',
        fontFamily: 'monospace',
        overflowX: 'auto',
        maxHeight: '260px',
        margin: 0
      }}>
        <code>{payloadText}</code>
      </pre>
    </div>
  );
};
