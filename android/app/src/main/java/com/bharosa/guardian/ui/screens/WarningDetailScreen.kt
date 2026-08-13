package com.bharosa.guardian.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bharosa.guardian.model.RiskAssessment
import com.bharosa.guardian.ui.components.HeaderBar
import com.bharosa.guardian.ui.components.WarningBanner
import com.bharosa.guardian.ui.theme.NavyPrimary

@Composable
fun WarningDetailScreen(
    assessment: RiskAssessment,
    lang: String,
    onPlayAudio: () -> Unit,
    onClose: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NavyPrimary)
    ) {
        HeaderBar(
            title = if (lang == "hi") "सुरक्षा अलर्ट" else "SECURITY ALERT",
            subtitle = if (lang == "hi") "वित्तीय धोखाधड़ी चेतावनी" else "FINANCIAL FRAUD DETECTED"
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            WarningBanner(
                assessment = assessment,
                lang = lang,
                onPlayAudio = onPlayAudio,
                onDismiss = onClose
            )
        }
    }
}
