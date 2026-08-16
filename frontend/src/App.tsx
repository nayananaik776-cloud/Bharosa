import React, { useState, useEffect } from 'react';
import { AnalysisRequest, AnalysisResponse, DemoScenario } from './types/risk';
import { analyzePayment } from './api/client';
import { Header } from './components/Header';
import { JudgeDemoBar } from './components/JudgeDemoBar';
import { SimulatedUpiApp } from './components/SimulatedUpiApp';
import { ApiInspector } from './components/ApiInspector';
import { ShieldCheck, Info, CheckCircle2 } from 'lucide-react';

const DEMO_SCENARIOS: DemoScenario[] = [
  {
    id: 'demo-1',
    title: 'Refund QR Scam',
    badge: 'HIGH',
    subtitle: 'Claims RECEIVE ₹5,000, transaction sends ₹5,000',
    requestPayload: {
      message: 'Your ₹5,000 refund has been approved. Scan this QR to receive your refund.',
      recipient: { name: 'Customer Support Ltd', upi_id: 'refunds@upi' },
      transaction: { amount: 5000, type: 'SEND', purpose: 'Refund Processing' },
      intent: { believed_action: 'RECEIVE', promised_benefit: 'Refund', promised_amount: 5000 },
      payment_history: []
    }
  },
  {
    id: 'demo-2',
    title: 'Pension Fee Scam',
    badge: 'HIGH',
    subtitle: 'Demands ₹2,000 up-front for ₹50,000 pension',
    requestPayload: {
      message: 'Your ₹50,000 pension has been approved. Pay ₹2,000 verification fee.',
      recipient: { name: 'Pension Officer', upi_id: 'verify@gov' },
      transaction: { amount: 2000, type: 'SEND', purpose: 'Verification Fee' },
      intent: { believed_action: 'SEND', promised_benefit: 'Pension', promised_amount: 50000 },
      payment_history: []
    }
  },
  {
    id: 'demo-3',
    title: 'Payment Escalation',
    badge: 'CRITICAL',
    subtitle: '₹1,000 → ₹2,000 → ₹5,000 escalating fees',
    requestPayload: {
      message: 'Pay ₹5,000 release fee to unlock your approved prize.',
      recipient: { name: 'Prize Manager', upi_id: 'prizes@upi' },
      transaction: { amount: 5000, type: 'SEND', purpose: 'Release Fee' },
      intent: { believed_action: 'SEND', promised_benefit: 'Prize', promised_amount: 100000 },
      payment_history: [
        { amount: 1000, purpose: 'Registration' },
        { amount: 2000, purpose: 'Verification' }
      ]
    }
  },
  {
    id: 'demo-4',
    title: 'Safe Payment',
    badge: 'LOW',
    subtitle: 'Standard grocery store transaction (₹450)',
    requestPayload: {
      message: 'Payment to Fresh Mart Supermarket',
      recipient: { name: 'Fresh Mart', upi_id: 'freshmart@upi' },
      transaction: { amount: 450, type: 'SEND', purpose: 'Groceries' },
      intent: { believed_action: 'SEND', promised_benefit: 'Groceries', promised_amount: 450 },
      payment_history: []
    }
  }
];

export const App: React.FC = () => {
  const [activeScenario, setActiveScenario] = useState<DemoScenario>(DEMO_SCENARIOS[0]);
  const [currentRequest, setCurrentRequest] = useState<AnalysisRequest>(DEMO_SCENARIOS[0].requestPayload);
  const [analysisResult, setAnalysisResult] = useState<AnalysisResponse | null>(null);
  const [isLoading, setIsLoading] = useState<boolean>(false);
  const [errorMessage, setErrorMessage] = useState<string | null>(null);
  const [isOffline, setIsOffline] = useState<boolean>(false);
  const [paymentCompleted, setPaymentCompleted] = useState<boolean>(false);
  const [isBackendConnected, setIsBackendConnected] = useState<boolean>(false);
  const [activeMobileTab, setActiveMobileTab] = useState<'app' | 'inspector'>('app');

  // Initial trigger for active scenario
  useEffect(() => {
    handleTriggerAnalysis(currentRequest);
  }, []);

  const handleSelectScenario = (scenario: DemoScenario) => {
    setActiveScenario(scenario);
    setCurrentRequest(scenario.requestPayload);
    setPaymentCompleted(false);
    handleTriggerAnalysis(scenario.requestPayload);
  };

  const handleTriggerAnalysis = async (req: AnalysisRequest = currentRequest) => {
    setIsLoading(true);
    setErrorMessage(null);
    setPaymentCompleted(false);

    const result = await analyzePayment(req);

    setIsLoading(false);
    setIsOffline(result.isOffline);
    setIsBackendConnected(!result.isOffline && !result.isFallback && result.data !== null);

    if (result.error) {
      setErrorMessage(result.error);
    } else {
      setErrorMessage(null);
    }

    if (result.data) {
      setAnalysisResult(result.data);
    } else {
      setAnalysisResult(null);
    }
  };

  const handleReset = () => {
    setAnalysisResult(null);
    setPaymentCompleted(false);
    setErrorMessage(null);
  };

  const handlePaymentSuccess = () => {
    setPaymentCompleted(true);
    setAnalysisResult(null);
  };

  return (
    <div className="app-container">
      {/* Header */}
      <Header
        isBackendConnected={isBackendConnected}
        onReset={() => handleSelectScenario(DEMO_SCENARIOS[0])}
      />

      {/* Judge Demo Control Panel */}
      <JudgeDemoBar
        scenarios={DEMO_SCENARIOS}
        activeScenarioId={activeScenario.id}
        onSelectScenario={handleSelectScenario}
      />

      {/* Mobile Tab Switcher Bar (visible only on mobile devices) */}
      <div className="mobile-tab-bar">
        <button
          className={`mobile-tab-btn ${activeMobileTab === 'app' ? 'active' : ''}`}
          onClick={() => setActiveMobileTab('app')}
        >
          📱 Payment Simulator
        </button>
        <button
          className={`mobile-tab-btn ${activeMobileTab === 'inspector' ? 'active' : ''}`}
          onClick={() => setActiveMobileTab('inspector')}
        >
          🔍 Signals & API Inspector
        </button>
      </div>

      {/* Main Grid: Phone Simulator & Explainer Dashboard */}
      <div className="main-grid">
        {/* Left Column: Phone App Simulator */}
        <div className={`grid-col-left ${activeMobileTab === 'app' ? 'mobile-visible' : 'mobile-hidden'}`}>

          {paymentCompleted ? (
            <div className="phone-wrapper">
              <div className="phone-frame" style={{ justifyContent: 'center', alignItems: 'center', padding: '2rem', textAlign: 'center' }}>
                <CheckCircle2 size={56} color="#10b981" style={{ marginBottom: '1rem' }} />
                <h3 style={{ fontSize: '1.3rem', color: '#ffffff', fontWeight: 800 }}>Payment Sent</h3>
                <p style={{ fontSize: '0.85rem', color: '#9ca3af', margin: '0.5rem 0 1.5rem 0' }}>
                  Transaction completed successfully.
                </p>
                <button
                  onClick={handleReset}
                  className="btn btn-primary"
                  style={{ fontSize: '0.85rem' }}
                >
                  Test Another Scenario
                </button>
              </div>
            </div>
          ) : (
            <SimulatedUpiApp
              request={currentRequest}
              analysisResult={analysisResult}
              isLoading={isLoading}
              errorMessage={errorMessage}
              isOffline={isOffline}
              onAnalyze={() => handleTriggerAnalysis(currentRequest)}
              onPaymentSuccess={handlePaymentSuccess}
              onReset={handleReset}
            />
          )}
        </div>

        {/* Right Column: Context Explainer & API Inspector */}
        <div className={`grid-col-right ${activeMobileTab === 'inspector' ? 'mobile-visible' : 'mobile-hidden'}`}>

          {/* Key Product Value Card */}
          <div className="glass-card" style={{ padding: '1.25rem', marginBottom: '1rem' }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', marginBottom: '0.65rem' }}>
              <Info size={20} color="#818cf8" />
              <h3 style={{ fontSize: '1.05rem', fontWeight: 700, margin: 0, color: '#ffffff' }}>
                BHAROSA CONTEXT ANALYSIS ENGINE
              </h3>
            </div>
            <p style={{ fontSize: '0.88rem', color: '#d1d5db', lineHeight: 1.5, margin: 0 }}>
              <em style={{ color: '#818cf8', fontStyle: 'normal', fontWeight: 600 }}>
                "Most systems analyze the transaction. Bharosa analyzes the story behind the transaction."
              </em>
            </p>
            <div style={{
              display: 'grid',
              gridTemplateColumns: 'repeat(auto-fit, minmax(130px, 1fr))',
              gap: '0.65rem',
              marginTop: '1rem'
            }}>
              <div style={{ background: 'rgba(255, 255, 255, 0.03)', padding: '0.5rem 0.65rem', borderRadius: '8px', border: '1px solid rgba(255, 255, 255, 0.05)' }}>
                <span style={{ fontSize: '0.65rem', color: '#9ca3af', display: 'block' }}>1. MESSAGE / CONTENT</span>
                <strong style={{ fontSize: '0.78rem', color: '#ffffff' }}>Financial SMS / Notification</strong>
              </div>
              <div style={{ background: 'rgba(255, 255, 255, 0.03)', padding: '0.5rem 0.65rem', borderRadius: '8px', border: '1px solid rgba(255, 255, 255, 0.05)' }}>
                <span style={{ fontSize: '0.65rem', color: '#9ca3af', display: 'block' }}>2. USER INTENT</span>
                <strong style={{ fontSize: '0.78rem', color: '#ffffff' }}>Believed Action vs Actual</strong>
              </div>
              <div style={{ background: 'rgba(255, 255, 255, 0.03)', padding: '0.5rem 0.65rem', borderRadius: '8px', border: '1px solid rgba(255, 255, 255, 0.05)' }}>
                <span style={{ fontSize: '0.65rem', color: '#9ca3af', display: 'block' }}>3. PROMISE VS PAYMENT</span>
                <strong style={{ fontSize: '0.78rem', color: '#ffffff' }}>Up-front Fee Detection</strong>
              </div>
              <div style={{ background: 'rgba(255, 255, 255, 0.03)', padding: '0.5rem 0.65rem', borderRadius: '8px', border: '1px solid rgba(255, 255, 255, 0.05)' }}>
                <span style={{ fontSize: '0.65rem', color: '#9ca3af', display: 'block' }}>4. PAYMENT HISTORY</span>
                <strong style={{ fontSize: '0.78rem', color: '#ffffff' }}>Escalation Tracking</strong>
              </div>
            </div>
          </div>

          {/* Current Active Scenario Info Card */}
          <div className="glass-card" style={{ padding: '1.25rem' }}>
            <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: '0.65rem' }}>
              <h4 style={{ fontSize: '0.95rem', fontWeight: 700, margin: 0, color: '#ffffff' }}>
                Active Scenario Parameters
              </h4>
              <span className={`badge ${
                activeScenario.badge === 'CRITICAL' ? 'badge-critical' :
                activeScenario.badge === 'HIGH' ? 'badge-high' : 'badge-low'
              }`}>
                {activeScenario.badge}
              </span>
            </div>

            <div style={{ fontSize: '0.82rem', color: '#9ca3af', display: 'flex', flexDirection: 'column', gap: '0.4rem' }}>
              <div>
                <strong style={{ color: '#d1d5db' }}>Message Context:</strong> "{currentRequest.message}"
              </div>
              <div>
                <strong style={{ color: '#d1d5db' }}>Recipient:</strong> {currentRequest.recipient.name} ({currentRequest.recipient.upi_id})
              </div>
              <div>
                <strong style={{ color: '#d1d5db' }}>Transaction:</strong> {currentRequest.transaction.type} ₹{currentRequest.transaction.amount} ({currentRequest.transaction.purpose || 'N/A'})
              </div>
              {currentRequest.payment_history && currentRequest.payment_history.length > 0 && (
                <div>
                  <strong style={{ color: '#d1d5db' }}>Prior Payments:</strong> {currentRequest.payment_history.map(h => `₹${h.amount} (${h.purpose})`).join(' → ')}
                </div>
              )}
            </div>
          </div>

          {/* Live API Inspector */}
          <ApiInspector request={currentRequest} response={analysisResult} />
        </div>
      </div>
    </div>
  );
};
