package com.bharosa.guardian.model

data class FinancialNotification(
    val id: String = System.currentTimeMillis().toString(),
    val packageName: String,
    val senderName: String,
    val title: String,
    val body: String,
    val fullText: String = "$title $body".trim(),
    val timestamp: Long = System.currentTimeMillis()
)
