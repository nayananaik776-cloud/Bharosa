package com.bharosa.guardian

import com.bharosa.guardian.data.remote.ApiClient
import com.bharosa.guardian.data.remote.dto.RiskAnalysisResponse
import com.bharosa.guardian.model.RiskLevel
import com.google.gson.Gson
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ApiClientTest {

    @Test
    fun `test risk analysis response DTO parsing`() {
        val json = """
            {
                "risk_score": 0.94,
                "risk_level": "HIGH",
                "category": "ADVANCE_FEE_SCAM",
                "is_scam": true,
                "warning_title": {
                    "en": "BE CAREFUL",
                    "hi": "सावधान रहें"
                },
                "warning_message": {
                    "en": "You are being asked to send money upfront.",
                    "hi": "आपसे पहले पैसे मांगे जा रहे हैं।"
                },
                "action_required": "DO_NOT_PAY",
                "explanation_audio_text": {
                    "en": "Do not send any money.",
                    "hi": "कोई पैसा न भेजें।"
                }
            }
        """.trimIndent()

        val gson = Gson()
        val responseDto = gson.fromJson(json, RiskAnalysisResponse::class.java)
        assertNotNull(responseDto)

        val domainModel = responseDto.toDomainModel("Test notification content")
        assertEquals(0.94, domainModel.riskScore, 0.001)
        assertEquals(RiskLevel.HIGH, domainModel.riskLevel)
        assertEquals("ADVANCE_FEE_SCAM", domainModel.category)
        assertTrue(domainModel.isScam)
        assertEquals("BE CAREFUL", domainModel.getTitle("en"))
        assertEquals("सावधान रहें", domainModel.getTitle("hi"))
        assertEquals("DO_NOT_PAY", domainModel.actionRequired)
    }

    @Test
    fun `test unreachable backend returns safe offline fallback response`() = runBlocking {
        val client = ApiClient("http://127.0.0.1:59999") // Unreachable port
        val assessment = client.analyzeNotification(
            text = "Pay ₹2,000 verification fee",
            sender = "Unknown",
            packageName = "com.test",
            language = "en"
        )

        assertNotNull(assessment)
        assertEquals(RiskLevel.UNKNOWN, assessment.riskLevel)
        assertFalse("Offline fallback must never declare payment safe", assessment.warningMessageEn.contains("Payment is safe"))
        assertTrue("Offline fallback must instruct user to verify before paying", assessment.warningMessageEn.contains("Unable to verify right now"))
    }
}
