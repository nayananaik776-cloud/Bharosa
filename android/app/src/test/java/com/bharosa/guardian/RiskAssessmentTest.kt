package com.bharosa.guardian

import com.bharosa.guardian.model.RiskAssessment
import com.bharosa.guardian.model.RiskLevel
import org.junit.Assert.assertEquals
import org.junit.Test

class RiskAssessmentTest {

    @Test
    fun `test localization accessor helpers`() {
        val assessment = RiskAssessment(
            riskScore = 0.9,
            riskLevel = RiskLevel.HIGH,
            category = "ADVANCE_FEE_SCAM",
            isScam = true,
            warningTitleEn = "BE CAREFUL",
            warningTitleHi = "सावधान रहें",
            warningMessageEn = "English Warning Message",
            warningMessageHi = "हिंदी चेतावनी संदेश",
            actionRequired = "DO_NOT_PAY",
            explanationAudioEn = "English Audio Explanation",
            explanationAudioHi = "हिंदी ऑडियो व्याख्या"
        )

        assertEquals("BE CAREFUL", assessment.getTitle("en"))
        assertEquals("सावधान रहें", assessment.getTitle("hi"))

        assertEquals("English Warning Message", assessment.getMessage("en"))
        assertEquals("हिंदी चेतावनी संदेश", assessment.getMessage("hi"))

        assertEquals("English Audio Explanation", assessment.getAudioText("en"))
        assertEquals("हिंदी ऑडियो व्याख्या", assessment.getAudioText("hi"))
    }
}
