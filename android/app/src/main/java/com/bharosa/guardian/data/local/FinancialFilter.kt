package com.bharosa.guardian.data.local

object FinancialFilter {

    private val FINANCIAL_KEYWORDS = listOf(
        "₹", "rs.", "rs", "inr", "rupees", "rupee",
        "pay", "paid", "payment", "upi", "gpay", "phonepe", "paytm",
        "transfer", "transferred", "refund", "cashback", "reward",
        "pension", "bank", "account", "a/c", "debit", "credited",
        "verification fee", "processing fee", "kyc", "blocked", "suspend",
        "urgent", "qr", "pin", "claim", "lottery", "prize", "winner",
        "bonus", "loan", "otp", "ebill", "electricity bill", "arrears"
    )

    private val EXCLUDE_KEYWORDS = listOf(
        "battery full", "battery low", "charging",
        "instagram", "facebook", "twitter", "snapchat", "tiktok", "youtube",
        "sent a photo", "sent a video", "liked your story", "started following",
        "weather", "update available", "download complete", "file saved",
        "bluetooth connected", "wifi connected", "alarm set", "timer finished",
        "missed call", "listening to", "now playing"
    )

    private val EXCLUDE_PACKAGES = setOf(
        "com.android.systemui",
        "com.google.android.youtube",
        "com.instagram.android",
        "com.facebook.katana",
        "com.snapchat.android",
        "com.twitter.android",
        "com.spotify.music"
    )

    fun isRelevantFinancial(
        packageName: String,
        title: String,
        body: String
    ): Boolean {
        val lowerPkg = packageName.lowercase()
        val combinedText = "$title $body".lowercase().trim()

        if (combinedText.isEmpty()) return false

        // Quick package exclusion check
        if (EXCLUDE_PACKAGES.contains(lowerPkg)) return false

        // Check if explicitly contains non-financial noise
        for (excludeKey in EXCLUDE_KEYWORDS) {
            if (combinedText.contains(excludeKey)) return false
        }

        // Must match at least one financial indicator keyword or symbol
        for (keyword in FINANCIAL_KEYWORDS) {
            if (combinedText.contains(keyword)) {
                return true
            }
        }

        return false
    }
}
