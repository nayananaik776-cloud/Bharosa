package com.bharosa.guardian.model

data class DemoScenario(
    val id: String,
    val titleEn: String,
    val titleHi: String,
    val descriptionEn: String,
    val descriptionHi: String,
    val notificationSender: String,
    val notificationTitle: String,
    val notificationBody: String,
    val expectedRiskLevel: RiskLevel,
    val expectedCategory: String,
    val mockAssessment: RiskAssessment
) {
    companion object {
        fun getPredefinedScenarios(): List<DemoScenario> {
            return listOf(
                DemoScenario(
                    id = "pension_scam",
                    titleEn = "Demo Pension Scam",
                    titleHi = "पेंशन घोटाला डेमो",
                    descriptionEn = "Targeting elderly users with promised pension bonus requiring upfront processing fee.",
                    descriptionHi = "अग्रिम प्रसंस्करण शुल्क मांगने वाली फर्जी पेंशन योजना।",
                    notificationSender = "Govt Pension Dept (WhatsApp)",
                    notificationTitle = "Pension Release Pending",
                    notificationBody = "Pay ₹2,500 verification fee to unlock your arrears of ₹75,000 monthly pension.",
                    expectedRiskLevel = RiskLevel.HIGH,
                    expectedCategory = "ADVANCE_FEE_SCAM",
                    mockAssessment = RiskAssessment(
                        riskScore = 0.94,
                        riskLevel = RiskLevel.HIGH,
                        category = "ADVANCE_FEE_SCAM",
                        isScam = true,
                        warningTitleEn = "BE CAREFUL",
                        warningTitleHi = "सावधान रहें",
                        warningMessageEn = "You are being asked to send ₹2,500 first for a promised ₹75,000 benefit.",
                        warningMessageHi = "आपको वादा किए गए ₹75,000 के लाभ के लिए पहले ₹2,500 भेजने के लिए कहा जा रहा है।",
                        actionRequired = "DO_NOT_PAY",
                        explanationAudioEn = "Warning! You are being asked to send ₹2,500 upfront for a promised pension payment. Legitimate pension departments never ask for verification fees. Do not pay.",
                        explanationAudioHi = "सावधान! आपसे पेंशन जारी करने के नाम पर ₹2,500 अग्रिम मांगे जा रहे हैं। कोई भी शुल्क न दें।"
                    )
                ),
                DemoScenario(
                    id = "refund_scam",
                    titleEn = "Demo Refund Scam",
                    titleHi = "रिफंड घोटाला डेमो",
                    descriptionEn = "Fake electricity board refund link asking for ₹1 verification transfer.",
                    descriptionHi = "₹1 ट्रांसफर कराकर खाता खाली करने वाला फर्जी बिजली रिफंड मेसेज।",
                    notificationSender = "Electricity Board (SMS)",
                    notificationTitle = "Bill Overpayment Refund",
                    notificationBody = "Your refund of ₹4,999 is ready. Pay ₹1 via UPI to verify your bank account details.",
                    expectedRiskLevel = RiskLevel.HIGH,
                    expectedCategory = "REFUND_SCAM",
                    mockAssessment = RiskAssessment(
                        riskScore = 0.91,
                        riskLevel = RiskLevel.HIGH,
                        category = "REFUND_SCAM",
                        isScam = true,
                        warningTitleEn = "BE CAREFUL",
                        warningTitleHi = "सावधान रहें",
                        warningMessageEn = "You are about to SEND money to receive a refund. To receive money, you NEVER enter your UPI PIN or pay ₹1.",
                        warningMessageHi = "आप रिफंड पाने के लिए पैसे भेजने वाले हैं। पैसे प्राप्त करने के लिए UPI पिन दर्ज करने की आवश्यकता नहीं होती।",
                        actionRequired = "DO_NOT_PAY",
                        explanationAudioEn = "Careful! You do not need to send money or scan a QR code to receive a refund. This is a refund scam.",
                        explanationAudioHi = "सावधान! रिफंड प्राप्त करने के लिए आपको कभी भी ₹1 या UPI पिन नहीं देना होता है।"
                    )
                ),
                DemoScenario(
                    id = "escalation_scam",
                    titleEn = "Demo Escalation Scam",
                    titleHi = "आपातकालीन खाता ब्लॉक डेमो",
                    descriptionEn = "Creates urgency claiming bank account will be blocked within 2 hours unless link clicked.",
                    descriptionHi = "2 घंटे में खाता बंद होने की धमकी देकर घबराहट में धोखाधड़ी।",
                    notificationSender = "HDFC Bank Alert (SMS)",
                    notificationTitle = "URGENT ACCOUNT SUSPENSION",
                    notificationBody = "Your HDFC account will be BLOCKED within 2 hours due to missing PAN. Click bit.ly/hdfc-kyc-verify immediately.",
                    expectedRiskLevel = RiskLevel.HIGH,
                    expectedCategory = "ACCOUNT_BLOCK_SCAM",
                    mockAssessment = RiskAssessment(
                        riskScore = 0.96,
                        riskLevel = RiskLevel.HIGH,
                        category = "ACCOUNT_BLOCK_SCAM",
                        isScam = true,
                        warningTitleEn = "BE CAREFUL",
                        warningTitleHi = "सावधान रहें",
                        warningMessageEn = "Scammers create artificial urgency ('Blocked in 2 hours'). Real banks do not send bit.ly links for KYC.",
                        warningMessageHi = "ठग जल्दीबाजी दिखाते हैं ('2 घंटे में ब्लॉक')। असली बैंक कभी भी लिंक के जरिए केवाईसी नहीं मांगते।",
                        actionRequired = "DO_NOT_CLICK",
                        explanationAudioEn = "Warning! Real banks never threaten to block your account in 2 hours via SMS links. Do not click the link.",
                        explanationAudioHi = "सावधान! असली बैंक एसएमएस लिंक से 2 घंटे में खाता बंद करने की धमकी कभी नहीं देते।"
                    )
                ),
                DemoScenario(
                    id = "safe_payment",
                    titleEn = "Demo Safe Payment",
                    titleHi = "सुरक्षित भुगतान डेमो",
                    descriptionEn = "Standard transaction receipt notification from legitimate payment app.",
                    descriptionHi = "प्रामाणिक भुगतान ऐप से सामान्य लेनदेन का संदेश।",
                    notificationSender = "PhonePe",
                    notificationTitle = "Payment Received",
                    notificationBody = "Received ₹500 from Rahul Sharma via PhonePe UPI. Txn ID: 40591029381.",
                    expectedRiskLevel = RiskLevel.SAFE,
                    expectedCategory = "SAFE_PAYMENT",
                    mockAssessment = RiskAssessment(
                        riskScore = 0.05,
                        riskLevel = RiskLevel.SAFE,
                        category = "SAFE_PAYMENT",
                        isScam = false,
                        warningTitleEn = "SAFE TRANSACTION",
                        warningTitleHi = "सुरक्षित लेनदेन",
                        warningMessageEn = "Standard payment received notification. No suspicious requests found.",
                        warningMessageHi = "सामान्य भुगतान प्राप्त हुआ। कोई संदिग्ध अनुरोध नहीं मिला।",
                        actionRequired = "SAFE",
                        explanationAudioEn = "This transaction is normal and safe.",
                        explanationAudioHi = "यह लेनदेन सामान्य और सुरक्षित है।"
                    )
                )
            )
        }
    }
}
