package com.evo.security.ui.screens

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.evo.security.R
import com.evo.security.ui.components.ModuleIcon

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onUrlCheckerClick: () -> Unit,
    onNewsFeedClick: () -> Unit
) {
    // Always use dark theme styling since we force dark theme
    val textColor = Color.White
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Evo Security",
                        fontWeight = FontWeight.Bold,
                        color = textColor
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
        containerColor = Color(0xFF1E1E1E)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                horizontalArrangement = Arrangement.spacedBy(24.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                item {
                    ModuleIcon(
                        iconRes = R.drawable.lock,
                        title = "URL & File Checker",
                        description = "Scan URLs and files for security threats",
                        onClick = onUrlCheckerClick
                    )
                }

                item {
                    ModuleIcon(
                        iconRes = R.drawable.news,
                        title = "Security News",
                        description = "Latest security alerts and threats",
                        onClick = onNewsFeedClick
                    )
                }
            }
        }
    }
}