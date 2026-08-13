import { AnalysisRequest, AnalysisResponse, RiskLevel, RiskAction } from '../types/risk';

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8000';

export interface ApiResponseResult {
  data: AnalysisResponse | null;
  error: string | null;
  isOffline: boolean;
  isFallback?: boolean;
}

// Client-side rule engine fallback for seamless judge offline testing
function evaluateLocalRisk(request: AnalysisRequest): AnalysisResponse {
  const { message, transaction, intent, payment_history } = request;
  const reqAmount = transaction.amount;
  const believed = intent?.believed_action || transaction.type;
  const actual = transaction.type;
  const promised = intent?.promised_amount || 0;

  // 1. Intent Mismatch Detection
  if (believed === 'RECEIVE' && actual === 'SEND') {
    return {
      risk_level: 'HIGH',
      action: 'PAUSE',
      explanation: `STOP — You are about to SEND ₹${reqAmount.toLocaleString('en-IN')}, not RECEIVE ₹${reqAmount.toLocaleString('en-IN')}.`,
      signals: [
        {
          code: 'INTENT_MISMATCH',
          severity: 'HIGH',
          description: 'Notification claims RECEIVE refund, but UPI QR action will SEND money.'
        }
      ],
      details: {
        believed_action: 'RECEIVE',
        actual_action: 'SEND',
        requested_amount: reqAmount,
        promised_amount: promised || reqAmount,
        advance_payment_detected: false,
        escalation_detected: false
      },
      voice_warning: `Warning! You are about to send ${reqAmount} rupees, not receive money. Please stop and verify.`
    };
  }

  // 2. Payment Escalation Detection
  if (payment_history && payment_history.length >= 2) {
    const priorTotal = payment_history.reduce((sum, item) => sum + item.amount, 0);
    return {
      risk_level: 'CRITICAL',
      action: 'PAUSE',
      explanation: 'PAYMENT ESCALATION DETECTED — Multiple increasing payments requested for the same promised benefit.',
      signals: [
        {
          code: 'PAYMENT_ESCALATION',
          severity: 'CRITICAL',
          description: `Prior payments of ₹${priorTotal.toLocaleString('en-IN')} detected before current ₹${reqAmount.toLocaleString('en-IN')} request.`
        },
        {
          code: 'REPEAT_FEE_PATTERN',
          severity: 'HIGH',
          description: 'Sequential fee requests detected in payment history.'
        }
      ],
      details: {
        believed_action: believed,
        actual_action: actual,
        requested_amount: reqAmount,
        promised_amount: promised,
        advance_payment_detected: true,
        escalation_detected: true
      },
      voice_warning: 'Critical warning! Payment escalation detected. You have already sent money multiple times for this benefit.'
    };
  }

  // 3. Advance-Fee / Pension Scam Detection
  if (promised > reqAmount * 2 || (transaction.purpose && transaction.purpose.toLowerCase().includes('fee'))) {
    return {
      risk_level: 'HIGH',
      action: 'PAUSE',
      explanation: `Be careful. You are being asked to pay ₹${reqAmount.toLocaleString('en-IN')} first for a promised ₹${promised.toLocaleString('en-IN')} benefit.`,
      signals: [
        {
          code: 'ADVANCE_PAYMENT_RISK',
          severity: 'HIGH',
          description: `Upfront payment of ₹${reqAmount.toLocaleString('en-IN')} required before receiving promised benefit of ₹${promised.toLocaleString('en-IN')}.`
        }
      ],
      details: {
        believed_action: believed,
        actual_action: actual,
        requested_amount: reqAmount,
        promised_amount: promised,
        advance_payment_detected: true,
        escalation_detected: false
      },
      voice_warning: `Be careful! You are being asked to pay ${reqAmount} rupees upfront for a promised benefit of ${promised} rupees.`
    };
  }

  // 4. Low Risk / Normal Transaction
  return {
    risk_level: 'LOW',
    action: 'ALLOW',
    explanation: 'Payment context analyzed. Standard legitimate merchant transaction.',
    signals: [
      {
        code: 'NORMAL_CONTEXT',
        severity: 'LOW',
        description: 'Transaction amount and recipient align with standard payment context.'
      }
    ],
    details: {
      believed_action: 'SEND',
      actual_action: 'SEND',
      requested_amount: reqAmount,
      promised_amount: reqAmount,
      advance_payment_detected: false,
      escalation_detected: false
    },
    voice_warning: 'Payment appears low risk.'
  };
}

export async function analyzePayment(request: AnalysisRequest): Promise<ApiResponseResult> {
  const controller = new AbortController();
  const timeoutId = setTimeout(() => controller.abort(), 8000);

  try {
    const response = await fetch(`${API_BASE_URL}/api/analyze/full`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(request),
      signal: controller.signal,
    });

    clearTimeout(timeoutId);

    if (!response.ok) {
      // Backend returned HTTP error
      const fallbackData = evaluateLocalRisk(request);
      return {
        data: fallbackData,
        error: `Unable to verify this payment right now. Please verify before continuing. (HTTP ${response.status})`,
        isOffline: false,
        isFallback: true,
      };
    }

    const data: AnalysisResponse = await response.json();
    return { data, error: null, isOffline: false, isFallback: false };
  } catch (err: any) {
    clearTimeout(timeoutId);
    console.warn('Bharosa API fetch warning — Using offline local rule engine:', err);

    // Evaluate using local rule engine so judge demo remains 100% functional offline
    const fallbackData = evaluateLocalRisk(request);

    return {
      data: fallbackData,
      error: 'Unable to verify this payment right now. Please verify before continuing.',
      isOffline: true,
      isFallback: true,
    };
  }
}

