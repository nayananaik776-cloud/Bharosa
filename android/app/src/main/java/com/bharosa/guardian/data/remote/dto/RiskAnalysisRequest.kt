package com.bharosa.guardian.data.remote.dto

import com.google.gson.annotations.SerializedName

data class RiskAnalysisRequest(
    @SerializedName("text") val text: String,
    @SerializedName("sender") val sender: String,
    @SerializedName("package_name") val packageName: String = "",
    @SerializedName("timestamp") val timestamp: String,
    @SerializedName("language") val language: String
)
