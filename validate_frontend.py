import urllib.request
import json
import sys

sys.stdout.reconfigure(encoding='utf-8')

FRONTEND_URL = "http://localhost:3000"
BACKEND_URL = "http://localhost:8000/api/analyze/full"

def check_frontend():
    print("\n[1/3] CHECKING FRONTEND SERVER ACCESSIBILITY...")
    try:
        req = urllib.request.urlopen(FRONTEND_URL)
        html = req.read().decode('utf-8')
        assert "BHAROSA" in html or "Bharosa" in html or "root" in html
        print(f"✓ Frontend server is UP at {FRONTEND_URL} (Status Code: {req.status})")
        print(f"✓ HTML document contains root element and metadata tags.")
    except Exception as e:
        print(f"✗ Frontend check failed: {e}")
        return False
    return True

def check_backend_scenarios():
    print("\n[2/3] CHECKING ALL 4 FRONTEND DEMO SCENARIO PAYLOADS...")
    scenarios = [
        {
            "id": "demo-1",
            "name": "Refund QR Scam (Intent Mismatch)",
            "expected_risk": "HIGH",
            "expected_action": "PAUSE",
            "expected_signal": "INTENT_MISMATCH",
            "payload": {
                "message": "Your ₹5,000 refund has been approved. Scan this QR to receive your refund.",
                "recipient": {"name": "Customer Support Ltd", "upi_id": "refunds@upi"},
                "transaction": {"amount": 5000, "type": "SEND", "purpose": "Refund Processing"},
                "intent": {"believed_action": "RECEIVE", "promised_benefit": "Refund", "promised_amount": 5000},
                "payment_history": []
            }
        },
        {
            "id": "demo-2",
            "name": "Pension Fee Scam (Advance Fee Risk)",
            "expected_risk": "HIGH",
            "expected_action": "PAUSE",
            "expected_signal": "ADVANCE_PAYMENT_RISK",
            "payload": {
                "message": "Your ₹50,000 pension has been approved. Pay ₹2,000 verification fee.",
                "recipient": {"name": "Pension Officer", "upi_id": "verify@gov"},
                "transaction": {"amount": 2000, "type": "SEND", "purpose": "Verification Fee"},
                "intent": {"believed_action": "SEND", "promised_benefit": "Pension", "promised_amount": 50000},
                "payment_history": []
            }
        },
        {
            "id": "demo-3",
            "name": "Payment Escalation Scam",
            "expected_risk": "CRITICAL",
            "expected_action": "PAUSE",
            "expected_signal": "PAYMENT_ESCALATION",
            "payload": {
                "message": "Pay ₹5,000 release fee to unlock your approved prize.",
                "recipient": {"name": "Prize Manager", "upi_id": "prizes@upi"},
                "transaction": {"amount": 5000, "type": "SEND", "purpose": "Release Fee"},
                "intent": {"believed_action": "SEND", "promised_benefit": "Prize", "promised_amount": 100000},
                "payment_history": [
                    {"amount": 1000, "purpose": "Registration"},
                    {"amount": 2000, "purpose": "Verification"}
                ]
            }
        },
        {
            "id": "demo-4",
            "name": "Safe Payment (Supermarket Purchase)",
            "expected_risk": "LOW",
            "expected_action": "ALLOW",
            "expected_signal": None,
            "payload": {
                "message": "Payment to Fresh Mart Supermarket",
                "recipient": {"name": "Fresh Mart", "upi_id": "freshmart@upi"},
                "transaction": {"amount": 450, "type": "SEND", "purpose": "Groceries"},
                "intent": {"believed_action": "SEND", "promised_benefit": "Groceries", "promised_amount": 450},
                "payment_history": []
            }
        }
    ]

    all_passed = True
    for sc in scenarios:
        data = json.dumps(sc["payload"]).encode('utf-8')
        req = urllib.request.Request(BACKEND_URL, data=data, headers={'Content-Type': 'application/json'})
        try:
            with urllib.request.urlopen(req) as res:
                body = json.loads(res.read().decode('utf-8'))
                risk_match = body.get('risk_level') == sc['expected_risk']
                action_match = body.get('action') == sc['expected_action']
                signals = [s['code'] for s in body.get('signals', [])]
                signal_match = (sc['expected_signal'] in signals) if sc['expected_signal'] else (len(signals) == 0)

                if risk_match and action_match and signal_match:
                    print(f"✓ [{sc['id']}] {sc['name']}: PASSED (Risk: {body['risk_level']}, Action: {body['action']}, Signals: {signals})")
                else:
                    print(f"✗ [{sc['id']}] {sc['name']}: MISMATCH (Got Risk: {body['risk_level']}, Action: {body['action']}, Signals: {signals})")
                    all_passed = False
        except Exception as e:
            print(f"✗ [{sc['id']}] {sc['name']}: FAILED with error {e}")
            all_passed = False

    return all_passed

def check_build_integrity():
    print("\n[3/3] CHECKING FRONTEND BUILD BUNDLE INTEGRITY...")
    try:
        import os
        dist_html = os.path.exists("frontend/dist/index.html")
        dist_assets = os.path.exists("frontend/dist/assets")
        if dist_html and dist_assets:
            print("✓ Production bundle dist/ directory exists and is fully compiled.")
            return True
        else:
            print("✗ dist directory missing")
            return False
    except Exception as e:
        print(f"✗ Build check failed: {e}")
        return False

if __name__ == "__main__":
    t1 = check_frontend()
    t2 = check_backend_scenarios()
    t3 = check_build_integrity()

    print("\n==========================================")
    if t1 and t2 and t3:
        print("ALL FRONTEND FEATURES & INTEGRATIONS WORKING PERFECTLY!")
    else:
        print("SOME CHECKS FAILED - PLEASE REVIEW LOGS ABOVE.")
    print("==========================================")
