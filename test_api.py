import urllib.request
import json
import sys

# Ensure UTF-8 output encoding for Windows terminal
sys.stdout.reconfigure(encoding='utf-8')

url = "http://127.0.0.1:8000/api/analyze/full"

scenarios = [
    {
        "name": "Demo 1 — Refund QR Scam (Intent Mismatch)",
        "payload": {
            "message": "Your ₹5,000 refund has been approved. Scan this QR to receive your refund.",
            "recipient": {"name": "Customer Support Ltd", "upi_id": "refunds@upi"},
            "transaction": {"amount": 5000, "type": "SEND", "purpose": "Refund Processing"},
            "intent": {"believed_action": "RECEIVE", "promised_benefit": "Refund", "promised_amount": 5000},
            "payment_history": []
        }
    },
    {
        "name": "Demo 2 — Pension Fee Scam (Advance Payment Risk)",
        "payload": {
            "message": "Your ₹50,000 pension has been approved. Pay ₹2,000 verification fee.",
            "recipient": {"name": "Pension Dept Officer", "upi_id": "verify@gov"},
            "transaction": {"amount": 2000, "type": "SEND", "purpose": "Verification Fee"},
            "intent": {"believed_action": "SEND", "promised_benefit": "Pension", "promised_amount": 50000},
            "payment_history": []
        }
    },
    {
        "name": "Demo 3 — Payment Escalation Scam",
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
        "name": "Demo 4 — Legitimate Grocery Payment",
        "payload": {
            "message": "Payment to Fresh Mart Supermarket",
            "recipient": {"name": "Fresh Mart", "upi_id": "freshmart@upi"},
            "transaction": {"amount": 450, "type": "SEND", "purpose": "Groceries"},
            "intent": {"believed_action": "SEND", "promised_benefit": "Groceries", "promised_amount": 450},
            "payment_history": []
        }
    }
]

for item in scenarios:
    print(f"\n==========================================")
    print(f"RUNNING TEST: {item['name']}")
    print(f"==========================================")
    data = json.dumps(item['payload']).encode('utf-8')
    req = urllib.request.Request(url, data=data, headers={'Content-Type': 'application/json'})
    try:
        with urllib.request.urlopen(req) as res:
            res_body = json.loads(res.read().decode('utf-8'))
            print(f"STATUS     : {res.status}")
            print(f"RISK LEVEL : {res_body.get('risk_level')}")
            print(f"ACTION     : {res_body.get('action')}")
            print(f"EXPLANATION: {res_body.get('explanation')}")
            print(f"SIGNALS    : {[s['code'] for s in res_body.get('signals', [])]}")
            print(f"VOICE WARN : {res_body.get('voice_warning')}")
    except Exception as e:
        print(f"FAILED: {e}")
