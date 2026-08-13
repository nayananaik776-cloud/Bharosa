package com.bharosa.guardian.service

import android.content.Intent
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import com.bharosa.guardian.BharosaApplication
import com.bharosa.guardian.data.local.FinancialFilter
import com.bharosa.guardian.data.remote.ApiClient
import com.bharosa.guardian.model.RiskLevel
import com.bharosa.guardian.model.ThreatLog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class GuardianNotificationListener : NotificationListenerService() {

    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private lateinit var apiClient: ApiClient

    override fun onCreate() {
        super.onCreate()
        val backendUrl = BharosaApplication.instance.preferences.backendUrl
        apiClient = ApiClient(backendUrl)
        Log.i(TAG, "GuardianNotificationListener service created.")
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)
        if (sbn == null) return

        val app = BharosaApplication.instance
        if (!app.preferences.isProtectionEnabled) return

        val packageName = sbn.packageName ?: ""
        if (packageName == applicationContext.packageName) return // Ignore self

        val extras = sbn.notification?.extras ?: return
        val title = extras.getCharSequence("android.title")?.toString() ?: ""
        val text = extras.getCharSequence("android.text")?.toString() ?: ""

        if (title.isBlank() && text.isBlank()) return

        app.preferences.incrementScanned()

        // 1. Local financial privacy filter
        val isRelevant = FinancialFilter.isRelevantFinancial(packageName, title, text)
        if (!isRelevant) {
            // Ignored silently for user privacy!
            return
        }

        // 2. Backend analysis for relevant financial notifications
        serviceScope.launch {
            val lang = app.preferences.selectedLanguage
            apiClient.updateBaseUrl(app.preferences.backendUrl)

            val fullText = "$title $text".trim()
            val assessment = apiClient.analyzeNotification(
                text = fullText,
                sender = packageName,
                packageName = packageName,
                language = lang
            )

            // 3. Warning & Voice explanation if high risk or offline fallback
            if (assessment.isScam || assessment.riskLevel == RiskLevel.HIGH || assessment.riskLevel == RiskLevel.UNKNOWN) {
                app.preferences.incrementBlocked()

                val threatLog = ThreatLog(
                    senderName = packageName,
                    rawContent = fullText,
                    riskAssessment = assessment
                )
                app.threatRepository.addLog(threatLog)

                // Voice warning
                val audioSpeech = assessment.getAudioText(lang)
                app.ttsManager.speak(audioSpeech, lang)

                // Broadcast warning to UI
                broadcastThreatAlert(threatLog)
            }
        }
    }

    private fun broadcastThreatAlert(log: ThreatLog) {
        val intent = Intent(ACTION_THREAT_DETECTED).apply {
            putExtra(EXTRA_THREAT_ID, log.id)
            setPackage(packageName)
        }
        sendBroadcast(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }

    companion object {
        private const val TAG = "GuardianListenerService"
        const val ACTION_THREAT_DETECTED = "com.bharosa.guardian.THREAT_DETECTED"
        const val EXTRA_THREAT_ID = "extra_threat_id"
    }
}
