package com.bharosa.guardian

import com.bharosa.guardian.data.local.FinancialFilter
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class FinancialFilterTest {

    @Test
    fun `test relevant financial notifications return true`() {
        val pensionScam = FinancialFilter.isRelevantFinancial(
            packageName = "com.whatsapp",
            title = "Pension Department",
            body = "Pay ₹2,500 verification fee to release ₹75,000 monthly pension"
        )
        assertTrue("Pension scam with fee and amount should be relevant", pensionScam)

        val refundScam = FinancialFilter.isRelevantFinancial(
            packageName = "com.android.mms",
            title = "Electricity Board Refund",
            body = "Your refund of ₹4,999 is pending. Pay ₹1 via UPI to verify account"
        )
        assertTrue("Refund scam with UPI and refund keywords should be relevant", refundScam)

        val accountBlock = FinancialFilter.isRelevantFinancial(
            packageName = "com.android.mms",
            title = "URGENT BANK ALERT",
            body = "Your account will be BLOCKED within 2 hours. Click link for KYC"
        )
        assertTrue("Urgent bank account block alert should be relevant", accountBlock)

        val upiReceived = FinancialFilter.isRelevantFinancial(
            packageName = "com.phonepe.app",
            title = "Payment Received",
            body = "Received ₹500 from Rahul via PhonePe UPI"
        )
        assertTrue("UPI payment transaction notification should be relevant", upiReceived)
    }

    @Test
    fun `test non-financial notifications return false`() {
        val batteryFull = FinancialFilter.isRelevantFinancial(
            packageName = "com.android.systemui",
            title = "Battery Status",
            body = "Battery full. Please unplug charger."
        )
        assertFalse("System battery full alert should be filtered out", batteryFull)

        val socialChat = FinancialFilter.isRelevantFinancial(
            packageName = "com.instagram.android",
            title = "Rahul",
            body = "Hey! Are we still meeting for lunch today?"
        )
        assertFalse("Social chat notification should be filtered out", socialChat)

        val musicApp = FinancialFilter.isRelevantFinancial(
            packageName = "com.spotify.music",
            title = "Now Playing",
            body = "Artist - Song Title"
        )
        assertFalse("Music playback notification should be filtered out", musicApp)

        val systemUI = FinancialFilter.isRelevantFinancial(
            packageName = "com.android.systemui",
            title = "Screenshot captured",
            body = "Tap to view screenshot"
        )
        assertFalse("System UI screenshot alert should be filtered out", systemUI)
    }
}
