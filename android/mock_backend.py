"""
Bharosa Standalone Mock Backend Server
Zero-dependency Python HTTP server implementing /api/v1/analyze for Android testing.
Run: python mock_backend.py
Listening on http://localhost:8000
"""

from http.server import HTTPServer, BaseHTTPRequestHandler
import json
import re

class BharosaMockHandler(BaseHTTPRequestHandler):

    def do_POST(self):
        if self.path == '/api/v1/analyze' or self.path == '/api/v1/analyze/':
            content_length = int(self.headers.get('Content-Length', 0))
            post_data = self.rfile.read(content_length)
            
            try:
                payload = json.loads(post_data.decode('utf-8'))
            except Exception:
                payload = {}

            text = payload.get('text', '').lower()
            lang = payload.get('language', 'en').lower()

            # Rule-based analysis for mock responses
            if any(k in text for k in ['pension', 'verification fee', 'advance fee']):
                response_data = {
                    "risk_score": 0.94,
                    "risk_level": "HIGH",
                    "category": "ADVANCE_FEE_SCAM",
                    "is_scam": True,
                    "warning_title": {
                        "en": "BE CAREFUL",
                        "hi": "सावधान रहें"
                    },
                    "warning_message": {
                        "en": "You are being asked to send money upfront for a promised benefit.",
                        "hi": "आपको वादा किए गए लाभ के लिए पहले पैसे भेजने के लिए कहा जा रहा है।"
                    },
                    "action_required": "DO_NOT_PAY",
                    "explanation_audio_text": {
                        "en": "Warning! Legitimate departments never ask for upfront verification fees. Do not send money.",
                        "hi": "सावधान! वैध विभाग कभी भी अग्रिम सत्यापन शुल्क नहीं मांगते। कोई पैसा न भेजें।"
                    }
                }
            elif any(k in text for k in ['refund', 'overpayment', 'electricity']):
                response_data = {
                    "risk_score": 0.91,
                    "risk_level": "HIGH",
                    "category": "REFUND_SCAM",
                    "is_scam": True,
                    "warning_title": {
                        "en": "BE CAREFUL",
                        "hi": "सावधान रहें"
                    },
                    "warning_message": {
                        "en": "You are about to SEND money to receive a refund. To receive money, you never pay.",
                        "hi": "आप रिफंड पाने के लिए पैसे भेजने वाले हैं। पैसे प्राप्त करने के लिए कभी भी भुगतान न करें।"
                    },
                    "action_required": "DO_NOT_PAY",
                    "explanation_audio_text": {
                        "en": "Careful! You do not need to send money to receive a refund. This is a refund scam.",
                        "hi": "सावधान! रिफंड प्राप्त करने के लिए आपको कभी भी पैसे देने की आवश्यकता नहीं होती।"
                    }
                }
            elif any(k in text for k in ['blocked', 'suspend', 'kyc', 'urgent']):
                response_data = {
                    "risk_score": 0.96,
                    "risk_level": "HIGH",
                    "category": "ACCOUNT_BLOCK_SCAM",
                    "is_scam": True,
                    "warning_title": {
                        "en": "BE CAREFUL",
                        "hi": "सावधान रहें"
                    },
                    "warning_message": {
                        "en": "Scammers create urgency ('Blocked in 2 hours'). Real banks do not send links via SMS.",
                        "hi": "ठग जल्दीबाजी दिखाते हैं ('2 घंटे में ब्लॉक')। असली बैंक कभी लिंक नहीं भेजते।"
                    },
                    "action_required": "DO_NOT_CLICK",
                    "explanation_audio_text": {
                        "en": "Warning! Real banks never threaten to block your account in 2 hours via SMS links.",
                        "hi": "सावधान! असली बैंक एसएमएस लिंक से 2 घंटे में खाता बंद करने की धमकी कभी नहीं देते।"
                    }
                }
            else:
                response_data = {
                    "risk_score": 0.05,
                    "risk_level": "SAFE",
                    "category": "SAFE_PAYMENT",
                    "is_scam": False,
                    "warning_title": {
                        "en": "SAFE TRANSACTION",
                        "hi": "सुरक्षित लेनदेन"
                    },
                    "warning_message": {
                        "en": "Standard payment transaction. No suspicious requests found.",
                        "hi": "सामान्य लेनदेन। कोई संदिग्ध अनुरोध नहीं मिला।"
                    },
                    "action_required": "SAFE",
                    "explanation_audio_text": {
                        "en": "This transaction is normal and safe.",
                        "hi": "यह लेनदेन सामान्य और सुरक्षित है।"
                    }
                }

            self.send_response(200)
            self.send_header('Content-Type', 'application/json; charset=utf-8')
            self.end_headers()
            self.wfile.write(json.dumps(response_data, ensure_ascii=False).encode('utf-8'))
        else:
            self.send_response(404)
            self.end_headers()

def run(server_class=HTTPServer, handler_class=BharosaMockHandler, port=8000):
    server_address = ('', port)
    httpd = server_class(server_address, handler_class)
    print(f"Bharosa Mock Backend listening on http://localhost:{port}/api/v1/analyze ...")
    try:
        httpd.serve_forever()
    except KeyboardInterrupt:
        print("\nStopping Mock Backend Server.")
        httpd.server_close()

if __name__ == '__main__':
    run()
