package com.evo.security.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.ui.graphics.Color
import com.evo.security.ui.theme.DarkCardBackground
import com.evo.security.ui.theme.LightCardBackground
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.evo.security.model.SecurityNews
import com.evo.security.model.SecuritySeverity
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun NewsFeedModule(
    news: List<SecurityNews>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
    ) {
        if (news.isEmpty()) {
            Text(
                text = "No news at this time",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(16.dp)
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxHeight(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(16.dp)
            ) {
                items(news) { newsItem ->
                    SecurityNewsItem(newsItem)
                }
            }
        }
    }
}

@Composable
private fun SecurityNewsItem(news: SecurityNews) {
    // Always use dark theme styling since we force dark theme
    val cardBackgroundColor = DarkCardBackground

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = cardBackgroundColor
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = news.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                SeverityBadge(severity = news.severity)
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = news.description,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Source: ${news.source}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Text(
                    text = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault())
                        .format(news.timestamp),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun SeverityBadge(severity: SecuritySeverity) {
    Box(
        modifier = Modifier
            .background(
                color = severity.color.copy(alpha = 0.1f),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = severity.displayName,
            style = MaterialTheme.typography.labelSmall,
            color = severity.color,
            fontWeight = FontWeight.Medium
        )
    }
}