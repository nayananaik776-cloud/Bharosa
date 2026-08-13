package com.bharosa.guardian.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bharosa.guardian.model.RiskAssessment
import com.bharosa.guardian.model.RiskLevel
import com.bharosa.guardian.ui.theme.NavySurface
import com.bharosa.guardian.ui.theme.SafeGreen
import com.bharosa.guardian.ui.theme.ScamRed
import com.bharosa.guardian.ui.theme.TextPrimary
import com.bharosa.guardian.ui.theme.WarningAmber

@Composable
fun WarningBanner(
    assessment: RiskAssessment,
    lang: String,
    onPlayAudio: () -> Unit,
    onDismiss: (() -> Unit)? = null
) {
    val cardBgColor = when (assessment.riskLevel) {
        RiskLevel.HIGH -> ScamRed.copy(alpha = 0.15f)
        RiskLevel.UNKNOWN -> WarningAmber.copy(alpha = 0.15f)
        RiskLevel.SAFE -> SafeGreen.copy(alpha = 0.15f)
        else -> WarningAmber.copy(alpha = 0.15f)
    }

    val headerBgColor = when (assessment.riskLevel) {
        RiskLevel.HIGH -> ScamRed
        RiskLevel.UNKNOWN -> WarningAmber
        RiskLevel.SAFE -> SafeGreen
        else -> WarningAmber
    }

    val icon = if (assessment.riskLevel == RiskLevel.SAFE) Icons.Default.Warning else Icons.Default.Warning

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardBgColor),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column {
            // Header Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(headerBgColor)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = "Alert",
                    tint = Color.White
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = assessment.getTitle(lang),
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    ),
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = onPlayAudio) {
                    Icon(
                        imageVector = Icons.Default.VolumeUp,
                        contentDescription = "Voice explanation",
                        tint = Color.White
                    )
                }
            }

            // Body content
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = assessment.getMessage(lang),
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                )

                if (assessment.rawTextAnalyzed.isNotBlank()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Card(
                        colors = CardDefaults.cardColors(containerColor = NavySurface),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "\"${assessment.rawTextAnalyzed}\"",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontSize = 13.sp,
                                color = TextPrimary.copy(alpha = 0.8f)
                            ),
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }

                if (onDismiss != null) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = headerBgColor)
                    ) {
                        Text(
                            text = if (lang == "hi") "समझ गया" else "UNDERSTOOD",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
