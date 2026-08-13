package com.bharosa.guardian.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.VolumeUp
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
import com.bharosa.guardian.ui.components.HeaderBar
import com.bharosa.guardian.ui.theme.GoldAccent
import com.bharosa.guardian.ui.theme.NavyCard
import com.bharosa.guardian.ui.theme.NavyPrimary
import com.bharosa.guardian.ui.theme.TextPrimary
import com.bharosa.guardian.ui.theme.TextSecondary

@Composable
fun LanguageScreen(
    currentLanguage: String,
    onLanguageSelected: (String) -> Unit,
    onTestAudio: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NavyPrimary)
    ) {
        HeaderBar(
            title = if (currentLanguage == "hi") "भाषा का चयन" else "LANGUAGE SELECTION",
            subtitle = "TTS VOICE & WARNINGS"
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = if (currentLanguage == "hi") "अपनी पसंदीदा चेतावनी भाषा चुनें:"
                else "Select your preferred warning and TTS voice language:",
                style = MaterialTheme.typography.bodyLarge.copy(color = TextSecondary)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Hindi Option
            LanguageCard(
                languageName = "हिन्दी (Hindi)",
                code = "hi",
                isSelected = (currentLanguage == "hi"),
                onSelect = { onLanguageSelected("hi") },
                onTestAudio = { onTestAudio("hi") }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // English Option
            LanguageCard(
                languageName = "English (Indian Accent)",
                code = "en",
                isSelected = (currentLanguage == "en"),
                onSelect = { onLanguageSelected("en") },
                onTestAudio = { onTestAudio("en") }
            )
        }
    }
}

@Composable
fun LanguageCard(
    languageName: String,
    code: String,
    isSelected: Boolean,
    onSelect: () -> Unit,
    onTestAudio: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() },
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) NavyCard else NavyCard.copy(alpha = 0.5f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (isSelected) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                    contentDescription = null,
                    tint = if (isSelected) GoldAccent else TextSecondary
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = languageName,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        fontSize = 18.sp
                    ),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onTestAudio,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = GoldAccent.copy(alpha = 0.2f))
            ) {
                Icon(
                    imageVector = Icons.Default.VolumeUp,
                    contentDescription = null,
                    tint = GoldAccent
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (code == "hi") "आवाज की जांच करें (Test Voice)" else "Test Voice Speech",
                    color = GoldAccent,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
