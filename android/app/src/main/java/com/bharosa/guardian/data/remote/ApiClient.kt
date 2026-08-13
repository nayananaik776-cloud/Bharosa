package com.bharosa.guardian.data.remote

import com.bharosa.guardian.data.remote.dto.RiskAnalysisRequest
import com.bharosa.guardian.data.remote.dto.RiskAnalysisResponse
import com.bharosa.guardian.model.RiskAssessment
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class ApiClient(private var baseUrl: String) {

    private val gson = Gson()
    private val connectTimeoutMs = 5000
    private val readTimeoutMs = 5000

    fun updateBaseUrl(newUrl: String) {
        baseUrl = newUrl.trimEnd('/')
    }

    suspend fun analyzeNotification(
        text: String,
        sender: String,
        packageName: String,
        language: String
    ): RiskAssessment = withContext(Dispatchers.IO) {
        try {
            val endpointUrl = if (baseUrl.endsWith("/api/v1/analyze")) {
                baseUrl
            } else {
                "${baseUrl.trimEnd('/')}/api/v1/analyze"
            }

            val isoFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US).apply {
                timeZone = TimeZone.getTimeZone("UTC")
            }
            val timestampStr = isoFormat.format(Date())

            val requestDto = RiskAnalysisRequest(
                text = text,
                sender = sender,
                packageName = packageName,
                timestamp = timestampStr,
                language = language
            )

            val jsonInputString = gson.toJson(requestDto)

            val url = URL(endpointUrl)
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "POST"
            conn.setRequestProperty("Content-Type", "application/json; utf-8")
            conn.setRequestProperty("Accept", "application/json")
            conn.connectTimeout = connectTimeoutMs
            conn.readTimeout = readTimeoutMs
            conn.doOutput = true

            OutputStreamWriter(conn.outputStream, "UTF-8").use { writer ->
                writer.write(jsonInputString)
                writer.flush()
            }

            val statusCode = conn.responseCode
            if (statusCode in 200..299) {
                BufferedReader(InputStreamReader(conn.inputStream, "UTF-8")).use { reader ->
                    val responseText = reader.readText()
                    val responseDto = gson.fromJson(responseText, RiskAnalysisResponse::class.java)
                    if (responseDto != null) {
                        return@withContext responseDto.toDomainModel(text)
                    }
                }
            }

            // Backend HTTP error or non-200 status -> Fallback
            RiskAssessment.createOfflineFallback(text, language)
        } catch (e: Exception) {
            // Timeout, network unreachable, connection refused -> Fallback
            RiskAssessment.createOfflineFallback(text, language)
        }
    }
}
