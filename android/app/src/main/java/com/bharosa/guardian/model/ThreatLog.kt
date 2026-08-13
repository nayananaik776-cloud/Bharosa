package com.bharosa.guardian.model

data class ThreatLog(
    val id: String = System.currentTimeMillis().toString(),
    val senderName: String,
    val rawContent: String,
    val riskAssessment: RiskAssessment,
    val timestamp: Long = System.currentTimeMillis()
)
