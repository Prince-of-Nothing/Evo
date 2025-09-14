package com.evo.security.ui.screens

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.evo.security.model.SecurityNews
import com.evo.security.ui.components.NewsFeedModule

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsFeedScreen(
    onBack: () -> Unit,
    securityNews: List<SecurityNews>
) {
    // Always use dark theme styling since we force dark theme
    val textColor = Color.White
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Security News",
                        fontWeight = FontWeight.Bold,
                        color = textColor
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = textColor
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
        containerColor = Color(0xFF1E1E1E)
    ) { innerPadding ->
        NewsFeedModule(
            news = securityNews,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        )
    }
}