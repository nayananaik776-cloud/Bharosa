package com.bharosa.guardian.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bharosa.guardian.model.DemoScenario
import com.bharosa.guardian.model.RiskLevel
import com.bharosa.guardian.ui.theme.GoldAccent
import com.bharosa.guardian.ui.theme.NavyCard
import com.bharosa.guardian.ui.theme.SafeGreen
import com.bharosa.guardian.ui.theme.ScamRed
import com.bharosa.guardian.ui.theme.TextPrimary
import com.bharosa.guardian.ui.theme.TextSecondary

@Composable
fun DemoCard(
    scenario: DemoScenario,
    lang: String,
    onSimulate: (DemoScenario) -> Unit
) {
    val badgeColor = when (scenario.expectedRiskLevel) {
        RiskLevel.HIGH -> ScamRed
        RiskLevel.SAFE -> SafeGreen
        else -> GoldAccent
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = NavyCard),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (lang == "hi") scenario.titleHi else scenario.titleEn,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    ),
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = scenario.expectedRiskLevel.name,
                    style = MaterialTheme.typography.labelLarge.copy(
                        color = badgeColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    ),
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = if (lang == "hi") scenario.descriptionHi else scenario.descriptionEn,
                style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Notification snippet preview
            Card(
                colors = CardDefaults.cardColors(containerColor = NavyCard.copy(alpha = 0.6f)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text(
                        text = "From: ${scenario.notificationSender}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = GoldAccent
                    )
                    Text(
                        text = scenario.notificationTitle,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = scenario.notificationBody,
                        fontSize = 12.sp,
                        color = TextPrimary.copy(alpha = 0.8f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = { onSimulate(scenario) },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = GoldAccent)
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Simulate",
                    tint = NavyCard
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (lang == "hi") "डेमो चलाएं" else "SIMULATE SCENARIO",
                    color = NavyCard,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
