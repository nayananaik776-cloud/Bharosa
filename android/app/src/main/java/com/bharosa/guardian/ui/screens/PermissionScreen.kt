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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Security
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
import com.bharosa.guardian.ui.theme.SafeGreen
import com.bharosa.guardian.ui.theme.ScamRed
import com.bharosa.guardian.ui.theme.TextPrimary
import com.bharosa.guardian.ui.theme.TextSecondary

@Composable
fun PermissionScreen(
    isNotificationListenerGranted: Boolean,
    lang: String,
    onRequestPermission: () -> Unit,
    onContinue: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NavyPrimary)
    ) {
        HeaderBar(
            title = if (lang == "hi") "अनुमति सेटअप" else "PERMISSION SETUP",
            subtitle = if (lang == "hi") "सुरक्षा सक्षम करें" else "ENABLE GUARDIAN"
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = NavyCard),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.NotificationsActive,
                            contentDescription = null,
                            tint = GoldAccent,
                            modifier = Modifier.padding(end = 12.dp)
                        )
                        Text(
                            text = if (lang == "hi") "नोटिफिकेशन एक्सेस अनुमति" else "Notification Listener Permission",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = if (lang == "hi")
                            "भरोसा को धोखाधड़ी वाले भुगतान संदेशों की स्वतः पहचान करने के लिए इस अनुमति की आवश्यकता है।"
                        else
                            "Bharosa Guardian needs Notification Access permission to detect financial scam messages automatically as they arrive.",
                        style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary)
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Status Indicator
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                if (isNotificationListenerGranted) SafeGreen.copy(alpha = 0.15f)
                                else ScamRed.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(12.dp)
                    ) {
                        Icon(
                            imageVector = if (isNotificationListenerGranted) Icons.Default.CheckCircle else Icons.Default.Security,
                            contentDescription = null,
                            tint = if (isNotificationListenerGranted) SafeGreen else ScamRed
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = if (isNotificationListenerGranted) {
                                if (lang == "hi") "अनुमति स्वीकृत (सक्रिय)" else "PERMISSION GRANTED (ACTIVE)"
                            } else {
                                if (lang == "hi") "अनुमति आवश्यक है" else "PERMISSION REQUIRED"
                            },
                            fontWeight = FontWeight.Bold,
                            color = if (isNotificationListenerGranted) SafeGreen else ScamRed,
                            fontSize = 13.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    if (!isNotificationListenerGranted) {
                        Button(
                            onClick = onRequestPermission,
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = GoldAccent),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(
                                text = if (lang == "hi") "सेटिंग्स में सक्षम करें" else "ENABLE IN SETTINGS",
                                color = NavyPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    } else {
                        Button(
                            onClick = onContinue,
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = SafeGreen),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(
                                text = if (lang == "hi") "आगे बढ़ें" else "CONTINUE TO DASHBOARD",
                                color = NavyPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}
