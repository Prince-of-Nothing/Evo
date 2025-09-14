package com.evo.security.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.evo.security.ui.theme.DarkCardBackground
import com.evo.security.ui.theme.LightCardBackground

@Composable
fun ModuleIcon(
    iconRes: Int? = null, // PNG drawable resource
    emoji: String? = null, // Fallback emoji
    title: String,
    description: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Always use dark theme styling since we force dark theme
    val textColor = Color.White
    val cardBackgroundColor = DarkCardBackground

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 56x56 card with just the icon
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(
                    color = cardBackgroundColor,
                    shape = RoundedCornerShape(12.dp)
                )
                .clickable { onClick() },
            contentAlignment = Alignment.Center
        ) {
            if (iconRes != null) {
                // Use PNG icon
                Icon(
                    painter = painterResource(id = iconRes),
                    contentDescription = null,
                    modifier = Modifier.size(28.dp),
                    tint = Color.Unspecified // Don't tint PNG icons
                )
            } else if (emoji != null) {
                // Fallback to emoji
                Text(
                    text = emoji,
                    fontSize = 28.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Title below the card like an app icon
        Text(
            text = title,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium,
            color = textColor,
            textAlign = TextAlign.Center,
            maxLines = 2,
            modifier = Modifier.width(72.dp)
        )
    }
}