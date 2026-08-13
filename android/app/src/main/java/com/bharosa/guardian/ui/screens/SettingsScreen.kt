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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bharosa.guardian.ui.components.HeaderBar
import com.bharosa.guardian.ui.theme.GoldAccent
import com.bharosa.guardian.ui.theme.NavyCard
import com.bharosa.guardian.ui.theme.NavyPrimary
import com.bharosa.guardian.ui.theme.NavySurface
import com.bharosa.guardian.ui.theme.TextPrimary
import com.bharosa.guardian.ui.theme.TextSecondary

@Composable
fun SettingsScreen(
    currentBackendUrl: String,
    lang: String,
    onSaveBackendUrl: (String) -> Unit
) {
    var urlText by remember { mutableStateOf(currentBackendUrl) }
    var isSavedMessageVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NavyPrimary)
    ) {
        HeaderBar(
            title = if (lang == "hi") "सेटिंग्स" else "SETTINGS",
            subtitle = if (lang == "hi") "सर्वर और गोपनीयता" else "CONFIG & PRIVACY"
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            // Backend URL Config Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = NavyCard),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Dns,
                            contentDescription = null,
                            tint = GoldAccent
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = if (lang == "hi") "बैकएंड सर्वर यूआरएल" else "Backend Server URL",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = if (lang == "hi") "एम्यूलेटर डिफ़ॉल्ट: http://10.0.2.2:8000"
                        else "Emulator Default: http://10.0.2.2:8000",
                        style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary, fontSize = 12.sp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = urlText,
                        onValueChange = {
                            urlText = it
                            isSavedMessageVisible = false
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = NavySurface,
                            unfocusedContainerColor = NavySurface,
                            focusedBorderColor = GoldAccent,
                            unfocusedBorderColor = TextSecondary,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            onSaveBackendUrl(urlText)
                            isSavedMessageVisible = true
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = GoldAccent)
                    ) {
                        Text(
                            text = if (lang == "hi") "सहेजें (Save)" else "SAVE BACKEND URL",
                            color = NavyPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    if (isSavedMessageVisible) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (lang == "hi") "यूआरएल सफलतापूर्वक सहेजा गया!" else "Backend URL updated successfully!",
                            color = GoldAccent,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Privacy Declaration Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = NavyCard),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = GoldAccent
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = if (lang == "hi") "गोपनीयता गारंटी" else "Privacy Guarantee",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = if (lang == "hi")
                            "• स्थानीय फिल्टर केवल वित्तीय संदेशों का चयन करता है।\n• निजी चैट (WhatsApp, Instagram, Battery) सर्वर को नहीं भेजी जाती हैं।\n• कोई संपर्क, स्थान या फ़ाइलें एकत्र नहीं की जाती हैं।"
                        else
                            "• On-device local filter runs first on Kotlin.\n• Personal messages & noise never leave device.\n• No contacts, location, or file access requested.",
                        style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary, fontSize = 13.sp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // About Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = NavyCard),
                shape = RoundedCornerShape(14.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = GoldAccent
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "BHAROSA GUARDIAN v1.0.0",
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Automatic Financial Fraud Protection for India",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }
                }
            }
        }
    }
}
