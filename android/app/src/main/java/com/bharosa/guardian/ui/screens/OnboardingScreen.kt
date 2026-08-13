package com.bharosa.guardian.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bharosa.guardian.ui.theme.GoldAccent
import com.bharosa.guardian.ui.theme.NavyCard
import com.bharosa.guardian.ui.theme.NavyPrimary
import com.bharosa.guardian.ui.theme.TextPrimary
import com.bharosa.guardian.ui.theme.TextSecondary

data class OnboardingStep(
    val titleEn: String,
    val titleHi: String,
    val descriptionEn: String,
    val descriptionHi: String,
    val icon: ImageVector
)

@Composable
fun OnboardingScreen(
    lang: String,
    onCompleteOnboarding: () -> Unit
) {
    val steps = listOf(
        OnboardingStep(
            titleEn = "Protection Comes To You",
            titleHi = "सुरक्षा खुद आपके पास आती है",
            descriptionEn = "No copy-pasting links or uploading screenshots. Bharosa scans incoming financial notifications automatically.",
            descriptionHi = "स्क्रीनशॉट अपलोड या लिंक कॉपी करने की आवश्यकता नहीं। भरोसा धोखाधड़ी वाले संदेशों को खुद पहचानता है।",
            icon = Icons.Default.NotificationsActive
        ),
        OnboardingStep(
            titleEn = "Strict Privacy First",
            titleHi = "गोपनीयता सबसे पहले",
            descriptionEn = "Lightweight on-device filtering ignores personal chats and system alerts. Zero personal data transmission.",
            descriptionHi = "व्यक्तिगत मैसेज और सिस्टम सूचनाएं डिवाइस पर ही छोड़ दी जाती हैं।",
            icon = Icons.Default.Lock
        ),
        OnboardingStep(
            titleEn = "Clear Voice Warnings",
            titleHi = "स्पष्ट आवाज में चेतावनी",
            descriptionEn = "Simple non-technical warnings spoken clearly in Hindi or English before you make any payment.",
            descriptionHi = "भुगतान करने से पहले हिंदी या अंग्रेजी में स्पष्ट आवाज में चेतावनी सुनें।",
            icon = Icons.Default.VolumeUp
        )
    )

    var currentStepIndex by remember { mutableStateOf(0) }
    val step = steps[currentStepIndex]

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NavyPrimary)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterVertically,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // App Title Header
        Column(horizontalAlignment = Alignment.CenterVertically) {
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "BHAROSA GUARDIAN",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = GoldAccent
                )
            )
            Text(
                text = if (lang == "hi") "स्वचालित वित्तीय सुरक्षा" else "Automatic Fraud Protection",
                style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary)
            )
        }

        // Center Step Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = NavyCard),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(GoldAccent.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = step.icon,
                        contentDescription = null,
                        tint = GoldAccent,
                        modifier = Modifier.size(40.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = if (lang == "hi") step.titleHi else step.titleEn,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        fontSize = 20.sp
                    ),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = if (lang == "hi") step.descriptionHi else step.descriptionEn,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = TextSecondary,
                        fontSize = 15.sp
                    ),
                    textAlign = TextAlign.Center
                )
            }
        }

        // Bottom Navigation
        Column(horizontalAlignment = Alignment.CenterVertically) {
            // Step indicator dots
            Row(horizontalArrangement = Arrangement.Center) {
                steps.indices.forEach { index ->
                    Box(
                        modifier = Modifier
                            .padding(4.dp)
                            .size(if (index == currentStepIndex) 12.dp else 8.dp)
                            .clip(CircleShape)
                            .background(if (index == currentStepIndex) GoldAccent else TextSecondary.copy(alpha = 0.4f))
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (currentStepIndex < steps.size - 1) {
                        currentStepIndex++
                    } else {
                        onCompleteOnboarding()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = GoldAccent),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = if (currentStepIndex == steps.size - 1) {
                        if (lang == "hi") "शुरू करें" else "GET STARTED"
                    } else {
                        if (lang == "hi") "आगे बढ़ें" else "NEXT"
                    },
                    color = NavyPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = null,
                    tint = NavyPrimary
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
