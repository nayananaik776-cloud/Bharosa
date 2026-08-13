package com.bharosa.guardian.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bharosa.guardian.ui.components.HeaderBar
import com.bharosa.guardian.ui.components.StatusBadge
import com.bharosa.guardian.ui.theme.GoldAccent
import com.bharosa.guardian.ui.theme.NavyCard
import com.bharosa.guardian.ui.theme.NavyPrimary
import com.bharosa.guardian.ui.theme.NavySurface
import com.bharosa.guardian.ui.theme.SafeGreen
import com.bharosa.guardian.ui.theme.ScamRed
import com.bharosa.guardian.ui.theme.TextPrimary
import com.bharosa.guardian.ui.theme.TextSecondary

@Composable
fun StatusScreen(
    isProtectionEnabled: Boolean,
    scannedCount: Int,
    blockedCount: Int,
    lang: String,
    onToggleProtection: () -> Unit,
    onNavigateDemo: () -> Unit,
    onNavigateLanguage: () -> Unit,
    onNavigateHistory: () -> Unit,
    onNavigateSettings: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NavyPrimary)
    ) {
        HeaderBar(
            title = "BHAROSA GUARDIAN",
            subtitle = if (lang == "hi") "सुरक्षा स्थिति" else "PROTECTION DASHBOARD"
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Main Shield Status Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = NavySurface),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(90.dp)
                            .clip(CircleShape)
                            .background(
                                if (isProtectionEnabled) SafeGreen.copy(alpha = 0.2f)
                                else ScamRed.copy(alpha = 0.2f)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = "Shield",
                            tint = if (isProtectionEnabled) SafeGreen else ScamRed,
                            modifier = Modifier.size(52.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    StatusBadge(
                        isActive = isProtectionEnabled,
                        activeText = if (lang == "hi") "सुरक्षा सक्रिय है" else "ACTIVE & PROTECTING",
                        inactiveText = if (lang == "hi") "सुरक्षा बंद है" else "PROTECTION PAUSED"
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = if (isProtectionEnabled) {
                            if (lang == "hi") "आपके डिवाइस की वित्तीय सूचनाएं सुरक्षित रूप से स्कैन हो रही हैं।"
                            else "Scanning incoming notifications locally for payment frauds."
                        } else {
                            if (lang == "hi") "सुरक्षा चालू करने के लिए बटन दबाएं।"
                            else "Tap to enable real-time fraud guardian."
                        },
                        style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary),
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = onToggleProtection,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isProtectionEnabled) ScamRed else SafeGreen
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PowerSettingsNew,
                            contentDescription = null,
                            tint = NavyPrimary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isProtectionEnabled) {
                                if (lang == "hi") "सुरक्षा बंद करें" else "PAUSE GUARDIAN"
                            } else {
                                if (lang == "hi") "सुरक्षा चालू करें" else "ENABLE GUARDIAN"
                            },
                            color = NavyPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Metrics Counters Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MetricCard(
                    modifier = Modifier.weight(1f),
                    title = if (lang == "hi") "स्कैन की गई सूचनाएं" else "Notifications Scanned",
                    count = scannedCount.toString(),
                    icon = Icons.Default.Security,
                    accentColor = GoldAccent
                )
                MetricCard(
                    modifier = Modifier.weight(1f),
                    title = if (lang == "hi") "रोके गए खतरे" else "Threats Blocked",
                    count = blockedCount.toString(),
                    icon = Icons.Default.Shield,
                    accentColor = ScamRed
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = if (lang == "hi") "त्वरित कार्य" else "QUICK ACTIONS",
                style = MaterialTheme.typography.labelLarge.copy(
                    color = GoldAccent,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Action Grid
            ActionRowButton(
                title = if (lang == "hi") "जज डेमो मोड (परिदृश्य परीक्षण)" else "Judge Demo Mode (Test Scenarios)",
                subtitle = if (lang == "hi") "पेंशन, रिफंड, क्यूआर घोटाला चलाएं" else "Run Pension, Refund, QR scam demos",
                icon = Icons.Default.BugReport,
                onClick = onNavigateDemo
            )

            ActionRowButton(
                title = if (lang == "hi") "भाषा चुनें (हिंदी / English)" else "Language Selection (English / Hindi)",
                subtitle = if (lang == "hi") "आवाज की भाषा बदलें" else "Change TTS voice accent",
                icon = Icons.Default.Language,
                onClick = onNavigateLanguage
            )

            ActionRowButton(
                title = if (lang == "hi") "खतरे की चेतावनी इतिहास" else "Threat Alert History",
                subtitle = if (lang == "hi") "पिछली चेतावनियों का रिकॉर्ड देखें" else "View past fraud detections",
                icon = Icons.Default.History,
                onClick = onNavigateHistory
            )

            ActionRowButton(
                title = if (lang == "hi") "बैकएंड यूआरएल और सेटिंग्स" else "Backend API & Settings",
                subtitle = if (lang == "hi") "सर्वर पता और प्राथमिकताएं" else "Configure server URL",
                icon = Icons.Default.Settings,
                onClick = onNavigateSettings
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun MetricCard(
    modifier: Modifier = Modifier,
    title: String,
    count: String,
    icon: ImageVector,
    accentColor: androidx.compose.ui.graphics.Color
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = NavyCard),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = accentColor,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = count,
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    fontSize = 24.sp
                )
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = TextSecondary,
                    fontSize = 11.sp
                )
            )
        }
    }
}

@Composable
fun ActionRowButton(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = NavyCard),
        shape = RoundedCornerShape(10.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = GoldAccent,
                modifier = Modifier.size(26.dp)
            )
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = TextPrimary
                    )
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                )
            }
        }
    }
}
