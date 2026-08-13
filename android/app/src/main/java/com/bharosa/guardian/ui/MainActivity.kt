package com.bharosa.guardian.ui

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.bharosa.guardian.BharosaApplication
import com.bharosa.guardian.data.remote.ApiClient
import com.bharosa.guardian.model.DemoScenario
import com.bharosa.guardian.model.RiskAssessment
import com.bharosa.guardian.model.ThreatLog
import com.bharosa.guardian.service.GuardianNotificationListener
import com.bharosa.guardian.ui.screens.DemoModeScreen
import com.bharosa.guardian.ui.screens.HistoryScreen
import com.bharosa.guardian.ui.screens.LanguageScreen
import com.bharosa.guardian.ui.screens.OnboardingScreen
import com.bharosa.guardian.ui.screens.PermissionScreen
import com.bharosa.guardian.ui.screens.SettingsScreen
import com.bharosa.guardian.ui.screens.StatusScreen
import com.bharosa.guardian.ui.screens.WarningDetailScreen
import com.bharosa.guardian.ui.theme.BharosaGuardianTheme
import com.bharosa.guardian.ui.theme.GoldAccent
import com.bharosa.guardian.ui.theme.NavyCard
import com.bharosa.guardian.ui.theme.NavyPrimary
import com.bharosa.guardian.ui.theme.TextPrimary
import com.bharosa.guardian.ui.theme.TextSecondary
import com.bharosa.guardian.util.PermissionUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

sealed class Screen(val titleEn: String, val titleHi: String, val icon: ImageVector) {
    object Status : Screen("Status", "स्थिति", Icons.Default.Security)
    object Demo : Screen("Demo", "डेमो", Icons.Default.BugReport)
    object Language : Screen("Language", "भाषा", Icons.Default.Language)
    object History : Screen("History", "इतिहास", Icons.Default.History)
    object Settings : Screen("Settings", "सेटिंग्स", Icons.Default.Settings)
}

class MainActivity : ComponentActivity() {

    private val app get() = BharosaApplication.instance
    private lateinit var apiClient: ApiClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate()
        apiClient = ApiClient(app.preferences.backendUrl)

        setContent {
            BharosaGuardianTheme {
                MainAppContainer()
            }
        }
    }

    @Composable
    private fun MainAppContainer() {
        var isOnboardingDone by remember { mutableStateOf(app.preferences.isOnboardingCompleted) }
        var isPermissionGranted by remember { mutableStateOf(PermissionUtils.isNotificationListenerGranted(this)) }
        var currentTab by remember { mutableIntStateOf(0) }
        var activeWarningAssessment by remember { mutableStateOf<RiskAssessment?>(null) }

        var lang by remember { mutableStateOf(app.preferences.selectedLanguage) }
        var isProtectionEnabled by remember { mutableStateOf(app.preferences.isProtectionEnabled) }
        var scannedCount by remember { mutableIntStateOf(app.preferences.scannedCount) }
        var blockedCount by remember { mutableIntStateOf(app.preferences.threatsBlockedCount) }
        var backendUrl by remember { mutableStateOf(app.preferences.backendUrl) }
        var logsList by remember { mutableStateOf(app.threatRepository.getLogs()) }

        // BroadcastReceiver for Live Threat Detection
        DisposableEffect(Unit) {
            val receiver = object : BroadcastReceiver() {
                override fun onReceive(context: Context?, intent: Intent?) {
                    if (intent?.action == GuardianNotificationListener.ACTION_THREAT_DETECTED) {
                        logsList = app.threatRepository.getLogs()
                        scannedCount = app.preferences.scannedCount
                        blockedCount = app.preferences.threatsBlockedCount

                        val latestLog = logsList.firstOrNull()
                        if (latestLog != null) {
                            activeWarningAssessment = latestLog.riskAssessment
                        }
                    }
                }
            }

            val filter = IntentFilter(GuardianNotificationListener.ACTION_THREAT_DETECTED)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                registerReceiver(receiver, filter, RECEIVER_NOT_EXPORTED)
            } else {
                registerReceiver(receiver, filter)
            }

            onDispose {
                unregisterReceiver(receiver)
            }
        }

        // Active Warning Overlay Screen
        if (activeWarningAssessment != null) {
            WarningDetailScreen(
                assessment = activeWarningAssessment!!,
                lang = lang,
                onPlayAudio = {
                    val speech = activeWarningAssessment!!.getAudioText(lang)
                    app.ttsManager.speak(speech, lang)
                },
                onClose = {
                    activeWarningAssessment = null
                }
            )
            return
        }

        // Onboarding flow
        if (!isOnboardingDone) {
            OnboardingScreen(
                lang = lang,
                onCompleteOnboarding = {
                    app.preferences.isOnboardingCompleted = true
                    isOnboardingDone = true
                }
            )
            return
        }

        // Permission Setup flow if not granted
        if (!isPermissionGranted) {
            PermissionScreen(
                isNotificationListenerGranted = isPermissionGranted,
                lang = lang,
                onRequestPermission = {
                    startActivity(PermissionUtils.getNotificationListenerSettingsIntent())
                },
                onContinue = {
                    isPermissionGranted = true
                }
            )
            return
        }

        // Main Dashboard & Tab Navigation
        val screens = listOf(Screen.Status, Screen.Demo, Screen.Language, Screen.History, Screen.Settings)

        Scaffold(
            bottomBar = {
                NavigationBar(
                    containerColor = NavyCard,
                    contentColor = TextPrimary
                ) {
                    screens.forEachIndexed { index, screen ->
                        NavigationBarItem(
                            selected = (currentTab == index),
                            onClick = { currentTab = index },
                            icon = {
                                Icon(
                                    imageVector = screen.icon,
                                    contentDescription = screen.titleEn,
                                    tint = if (currentTab == index) GoldAccent else TextSecondary
                                )
                            },
                            label = {
                                Text(
                                    text = if (lang == "hi") screen.titleHi else screen.titleEn,
                                    fontSize = 11.sp,
                                    fontWeight = if (currentTab == index) FontWeight.Bold else FontWeight.Normal,
                                    color = if (currentTab == index) GoldAccent else TextSecondary
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = NavyPrimary
                            )
                        )
                    }
                }
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                when (screens[currentTab]) {
                    Screen.Status -> StatusScreen(
                        isProtectionEnabled = isProtectionEnabled,
                        scannedCount = scannedCount,
                        blockedCount = blockedCount,
                        lang = lang,
                        onToggleProtection = {
                            val newStatus = !isProtectionEnabled
                            app.preferences.isProtectionEnabled = newStatus
                            isProtectionEnabled = newStatus
                        },
                        onNavigateDemo = { currentTab = 1 },
                        onNavigateLanguage = { currentTab = 2 },
                        onNavigateHistory = { currentTab = 3 },
                        onNavigateSettings = { currentTab = 4 }
                    )

                    Screen.Demo -> DemoModeScreen(
                        lang = lang,
                        onSimulateScenario = { scenario ->
                            runDemoScenario(
                                scenario = scenario,
                                lang = lang,
                                onResult = { assessment ->
                                    scannedCount = app.preferences.scannedCount + 1
                                    app.preferences.scannedCount = scannedCount

                                    if (assessment.isScam || assessment.riskLevel.name == "HIGH" || assessment.riskLevel.name == "UNKNOWN") {
                                        blockedCount = app.preferences.threatsBlockedCount + 1
                                        app.preferences.threatsBlockedCount = blockedCount

                                        val threatLog = ThreatLog(
                                            senderName = scenario.notificationSender,
                                            rawContent = "${scenario.notificationTitle} ${scenario.notificationBody}",
                                            riskAssessment = assessment
                                        )
                                        app.threatRepository.addLog(threatLog)
                                        logsList = app.threatRepository.getLogs()
                                    }

                                    // Voice read aloud
                                    val speech = assessment.getAudioText(lang)
                                    app.ttsManager.speak(speech, lang)

                                    // Show warning detail screen
                                    activeWarningAssessment = assessment
                                }
                            )
                        }
                    )

                    Screen.Language -> LanguageScreen(
                        currentLanguage = lang,
                        onLanguageSelected = { newLang ->
                            app.preferences.selectedLanguage = newLang
                            lang = newLang
                        },
                        onTestAudio = { testLang ->
                            val sampleMsg = if (testLang == "hi")
                                "सावधान! भरोसा गार्डियन आपकी वित्तीय सुरक्षा के लिए तैयार है।"
                            else
                                "Warning! Bharosa Guardian is active and protecting your financial payments."
                            app.ttsManager.speak(sampleMsg, testLang)
                        }
                    )

                    Screen.History -> HistoryScreen(
                        logs = logsList,
                        lang = lang,
                        onPlayLogAudio = { log ->
                            val speech = log.riskAssessment.getAudioText(lang)
                            app.ttsManager.speak(speech, lang)
                        },
                        onClearHistory = {
                            app.threatRepository.clearLogs()
                            logsList = emptyList()
                        }
                    )

                    Screen.Settings -> SettingsScreen(
                        currentBackendUrl = backendUrl,
                        lang = lang,
                        onSaveBackendUrl = { newUrl ->
                            app.preferences.backendUrl = newUrl
                            backendUrl = newUrl
                            apiClient.updateBaseUrl(newUrl)
                        }
                    )
                }
            }
        }
    }

    private fun runDemoScenario(
        scenario: DemoScenario,
        lang: String,
        onResult: (RiskAssessment) -> Unit
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            // Try calling API first, fallback to mock assessment if server unreachable
            val realAssessment = apiClient.analyzeNotification(
                text = "${scenario.notificationTitle} ${scenario.notificationBody}",
                sender = scenario.notificationSender,
                packageName = "demo.test.package",
                language = lang
            )

            val finalAssessment = if (realAssessment.riskLevel.name == "UNKNOWN") {
                // If backend offline in demo mode, use scenario's predefined mock assessment for instant demonstration!
                scenario.mockAssessment
            } else {
                realAssessment
            }

            CoroutineScope(Dispatchers.Main).launch {
                onResult(finalAssessment)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Refresh permissions status on return to app
        val granted = PermissionUtils.isNotificationListenerGranted(this)
        if (granted != app.preferences.isProtectionEnabled) {
            app.preferences.isProtectionEnabled = granted
        }
    }
}
