package com.bharosa.guardian

import com.bharosa.guardian.model.DemoScenario
import com.bharosa.guardian.model.RiskLevel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class DemoScenariosTest {

    @Test
    fun `test predefined demo scenarios exist and contain valid data`() {
        val scenarios = DemoScenario.getPredefinedScenarios()
        assertEquals(4, scenarios.size)

        val pensionScam = scenarios.find { it.id == "pension_scam" }
        assertNotNull(pensionScam)
        assertEquals(RiskLevel.HIGH, pensionScam?.expectedRiskLevel)
        assertEquals("ADVANCE_FEE_SCAM", pensionScam?.expectedCategory)
        assertTrue(pensionScam?.titleEn?.contains("Pension") == true)
        assertTrue(pensionScam?.mockAssessment?.warningTitleEn == "BE CAREFUL")

        val refundScam = scenarios.find { it.id == "refund_scam" }
        assertNotNull(refundScam)
        assertEquals(RiskLevel.HIGH, refundScam?.expectedRiskLevel)

        val escalationScam = scenarios.find { it.id == "escalation_scam" }
        assertNotNull(escalationScam)
        assertEquals(RiskLevel.HIGH, escalationScam?.expectedRiskLevel)

        val safePayment = scenarios.find { it.id == "safe_payment" }
        assertNotNull(safePayment)
        assertEquals(RiskLevel.SAFE, safePayment?.expectedRiskLevel)
    }
}
