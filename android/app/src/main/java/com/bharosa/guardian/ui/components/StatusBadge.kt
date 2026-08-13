package com.bharosa.guardian.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bharosa.guardian.ui.theme.SafeGreen
import com.bharosa.guardian.ui.theme.ScamRed
import com.bharosa.guardian.ui.theme.TextPrimary

@Composable
fun StatusBadge(
    isActive: Boolean,
    activeText: String = "ACTIVE & PROTECTING",
    inactiveText: String = "PROTECTION PAUSED"
) {
    val bgColor = if (isActive) SafeGreen.copy(alpha = 0.2f) else ScamRed.copy(alpha = 0.2f)
    val dotColor = if (isActive) SafeGreen else ScamRed
    val text = if (isActive) activeText else inactiveText

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(bgColor)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(dotColor)
            )
            Text(
                text = text,
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}
