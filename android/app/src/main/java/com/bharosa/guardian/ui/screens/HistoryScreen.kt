package com.bharosa.guardian.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bharosa.guardian.model.ThreatLog
import com.bharosa.guardian.ui.components.HeaderBar
import com.bharosa.guardian.ui.theme.GoldAccent
import com.bharosa.guardian.ui.theme.NavyCard
import com.bharosa.guardian.ui.theme.NavyPrimary
import com.bharosa.guardian.ui.theme.ScamRed
import com.bharosa.guardian.ui.theme.TextPrimary
import com.bharosa.guardian.ui.theme.TextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HistoryScreen(
    logs: List<ThreatLog>,
    lang: String,
    onPlayLogAudio: (ThreatLog) -> Unit,
    onClearHistory: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NavyPrimary)
    ) {
        HeaderBar(
            title = if (lang == "hi") "चेतावनी इतिहास" else "ALERT HISTORY",
            subtitle = if (lang == "hi") "रोके गए खतरे" else "BLOCKED FRAUD LOGS"
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            if (logs.isNotEmpty()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${logs.size} ${if (lang == "hi") "खतरे दर्ज किए गए" else "Threats Recorded"}",
                        style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary),
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = onClearHistory) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Clear",
                            tint = ScamRed
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            if (logs.isEmpty()) {
                Spacer(modifier = Modifier.height(40.dp))
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        tint = GoldAccent.copy(alpha = 0.5f),
                        modifier = Modifier.padding(16.dp)
                    )
                    Text(
                        text = if (lang == "hi") "अभी तक कोई खतरा दर्ज नहीं हुआ।" else "No fraud threats detected yet.",
                        style = MaterialTheme.typography.bodyLarge.copy(color = TextSecondary)
                    )
                    Text(
                        text = if (lang == "hi") "आपकी सुरक्षा सक्रिय है।" else "Your live notification guardian is active.",
                        style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary.copy(alpha = 0.7f))
                    )
                }
            } else {
                LazyColumn {
                    items(logs) { log ->
                        ThreatLogCard(
                            log = log,
                            lang = lang,
                            onPlayAudio = { onPlayLogAudio(log) }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun ThreatLogCard(
    log: ThreatLog,
    lang: String,
    onPlayAudio: () -> Unit
) {
    val dateStr = try {
        val sdf = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())
        sdf.format(Date(log.timestamp))
    } catch (e: Exception) {
        ""
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = NavyCard),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = ScamRed
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = log.riskAssessment.getTitle(lang),
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = ScamRed,
                        fontSize = 15.sp
                    ),
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = dateStr,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = log.riskAssessment.getMessage(lang),
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = TextPrimary,
                    fontWeight = FontWeight.SemiBold
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "From: ${log.senderName}",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = GoldAccent,
                        fontSize = 11.sp
                    ),
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = onPlayAudio) {
                    Icon(
                        imageVector = Icons.Default.VolumeUp,
                        contentDescription = "Replay audio",
                        tint = GoldAccent
                    )
                }
            }
        }
    }
}
