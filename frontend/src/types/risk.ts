export type RiskLevel = 'LOW' | 'MEDIUM' | 'HIGH' | 'CRITICAL';
export type RiskAction = 'ALLOW' | 'VERIFY' | 'PAUSE';

export interface Recipient {
  name: string;
  upi_id: string;
}

export interface Transaction {
  amount: number;
  type: 'SEND' | 'RECEIVE';
  purpose?: string;
}

export interface Intent {
  believed_action?: 'SEND' | 'RECEIVE';
  promised_benefit?: string;
  promised_amount?: number;
}

export interface PaymentHistoryItem {
  amount: number;
  purpose?: string;
}

export interface AnalysisRequest {
  message: string;
  recipient: Recipient;
  transaction: Transaction;
  intent?: Intent;
  payment_history?: PaymentHistoryItem[];
}

export interface RiskSignal {
  code: string;
  severity: 'LOW' | 'MEDIUM' | 'HIGH' | 'CRITICAL';
  description: string;
}

export interface AnalysisDetails {
  believed_action: string;
  actual_action: string;
  requested_amount: number;
  promised_amount: number;
  advance_payment_detected: boolean;
  escalation_detected: boolean;
}

export interface AnalysisResponse {
  risk_level: RiskLevel;
  action: RiskAction;
  explanation: string;
  signals: RiskSignal[];
  details: AnalysisDetails;
  voice_warning?: string;
}

export interface DemoScenario {
  id: string;
  title: string;
  badge: string;
  subtitle: string;
  requestPayload: AnalysisRequest;
}
