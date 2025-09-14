package com.evo.security.model

import java.util.Date

data class SecurityNews(
    val id: String,
    val title: String,
    val description: String,
    val severity: SecuritySeverity,
    val timestamp: Date,
    val source: String,
    val url: String? = null
)

enum class SecuritySeverity(val color: androidx.compose.ui.graphics.Color, val displayName: String) {
    LOW(androidx.compose.ui.graphics.Color.Green, "Low"),
    MEDIUM(androidx.compose.ui.graphics.Color(0xFFFF9800), "Medium"), // Orange
    HIGH(androidx.compose.ui.graphics.Color.Red, "High"),
    CRITICAL(androidx.compose.ui.graphics.Color(0xFF8B0000), "Critical") // Dark Red
}

data class FileCheckRequest(
    val fileName: String? = null,
    val fileUri: String? = null,
    val fileHash: String? = null,
    val url: String? = null,
    val timestamp: Date = Date()
)

data class FileCheckResult(
    val isSafe: Boolean,
    val threats: List<String> = emptyList(),
    val scanEngine: String,
    val timestamp: Date = Date()
)