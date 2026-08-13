package com.bharosa.guardian.data.remote.dto

import com.bharosa.guardian.model.RiskAssessment
import com.bharosa.guardian.model.RiskLevel
import com.google.gson.annotations.SerializedName

data class RiskAnalysisResponse(
    @SerializedName("risk_score") val riskScore: Double?,
    @SerializedName("risk_level") val riskLevel: String?,
    @SerializedName("category") val category: String?,
    @SerializedName("is_scam") val isScam: Boolean?,
    @SerializedName("warning_title") val warningTitle: Map<String, String>?,
    @SerializedName("warning_message") val warningMessage: Map<String, String>?,
    @SerializedName("action_required") val actionRequired: String?,
    @SerializedName("explanation_audio_text") val explanationAudioText: Map<String, String>?
) {
    fun toDomainModel(rawText: String): RiskAssessment {
        val level = try {
            RiskLevel.valueOf(riskLevel?.uppercase() ?: "UNKNOWN")
        } catch (e: Exception) {
            if ((riskScore ?: 0.0) >= 0.7) RiskLevel.HIGH else RiskLevel.LOW
        }

        val titleEn = warningTitle?.get("en") ?: "BE CAREFUL"
        val titleHi = warningTitle?.get("hi") ?: "सावधान रहें"

        val msgEn = warningMessage?.get("en") ?: "Suspicious financial request detected."
        val msgHi = warningMessage?.get("hi") ?: "संदिग्ध वित्तीय अनुरोध का पता चला।"

        val audioEn = explanationAudioText?.get("en") ?: msgEn
        val audioHi = explanationAudioText?.get("hi") ?: msgHi

        return RiskAssessment(
            riskScore = riskScore ?: 0.5,
            riskLevel = level,
            category = category ?: "UNCLASSIFIED",
            isScam = isScam ?: (level == RiskLevel.HIGH),
            warningTitleEn = titleEn,
            warningTitleHi = titleHi,
            warningMessageEn = msgEn,
            warningMessageHi = msgHi,
            actionRequired = actionRequired ?: "DO_NOT_PAY",
            explanationAudioEn = audioEn,
            explanationAudioHi = audioHi,
            rawTextAnalyzed = rawText
        )
    }
}
