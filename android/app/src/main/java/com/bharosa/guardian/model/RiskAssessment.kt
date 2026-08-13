package com.bharosa.guardian.model

enum class RiskLevel {
    HIGH,
    MEDIUM,
    LOW,
    SAFE,
    UNKNOWN
}

data class RiskAssessment(
    val riskScore: Double,
    val riskLevel: RiskLevel,
    val category: String,
    val isScam: Boolean,
    val warningTitleEn: String,
    val warningTitleHi: String,
    val warningMessageEn: String,
    val warningMessageHi: String,
    val actionRequired: String,
    val explanationAudioEn: String,
    val explanationAudioHi: String,
    val rawTextAnalyzed: String = ""
) {
    fun getTitle(lang: String): String {
        return if (lang.lowercase() == "hi") warningTitleHi else warningTitleEn
    }

    fun getMessage(lang: String): String {
        return if (lang.lowercase() == "hi") warningMessageHi else warningMessageEn
    }

    fun getAudioText(lang: String): String {
        return if (lang.lowercase() == "hi") explanationAudioHi else explanationAudioEn
    }

    companion object {
        fun createOfflineFallback(text: String, lang: String = "en"): RiskAssessment {
            return RiskAssessment(
                riskScore = 0.5,
                riskLevel = RiskLevel.UNKNOWN,
                category = "UNVERIFIED_FINANCIAL",
                isScam = false,
                warningTitleEn = "VERIFY BEFORE PAYING",
                warningTitleHi = "भुगतान करने से पहले जांच करें",
                warningMessageEn = "Unable to verify right now. Please verify before paying.",
                warningMessageHi = "अभी पुष्टि करने में असमर्थ। कृपया भुगतान करने से पहले जांच लें।",
                actionRequired = "VERIFY_FIRST",
                explanationAudioEn = "Unable to verify message right now. Please verify before paying.",
                explanationAudioHi = "अभी संदेश की पुष्टि करने में असमर्थ। कृपया भुगतान करने से पहले जांच लें।",
                rawTextAnalyzed = text
            )
        }
    }
}
