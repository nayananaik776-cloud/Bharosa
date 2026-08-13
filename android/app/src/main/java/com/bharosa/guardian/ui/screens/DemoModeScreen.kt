package com.bharosa.guardian.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bharosa.guardian.model.DemoScenario
import com.bharosa.guardian.ui.components.DemoCard
import com.bharosa.guardian.ui.components.HeaderBar
import com.bharosa.guardian.ui.theme.NavyPrimary
import com.bharosa.guardian.ui.theme.TextSecondary

@Composable
fun DemoModeScreen(
    lang: String,
    onSimulateScenario: (DemoScenario) -> Unit
) {
    val scenarios = DemoScenario.getPredefinedScenarios()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NavyPrimary)
    ) {
        HeaderBar(
            title = if (lang == "hi") "जज डेमो मोड" else "JUDGE DEMO MODE",
            subtitle = if (lang == "hi") "घोटाला परिदृश्य सिमुलेशन" else "SIMULATE FRAUD SCENARIOS"
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = if (lang == "hi")
                    "वास्तविक व्हाट्सएप/एसएमएस संदेशों के बिना भरोसा गार्डियन का परीक्षण करने के लिए किसी भी परिदृश्य का चयन करें:"
                else
                    "Select any scenario to test Bharosa Guardian's live protection and voice warnings without needing real WhatsApp messages:",
                style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary)
            )

            Spacer(modifier = Modifier.height(12.dp))

            scenarios.forEach { scenario ->
                DemoCard(
                    scenario = scenario,
                    lang = lang,
                    onSimulate = onSimulateScenario
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
