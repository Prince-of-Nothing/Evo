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
import com.evo.security.model.FileCheckRequest
import com.evo.security.model.FileCheckResult
import com.evo.security.repository.SecurityRepository
import com.evo.security.ui.components.FileCheckerModule
import com.evo.security.ui.theme.EvoBlue
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UrlCheckerScreen(
    onBack: () -> Unit,
    securityRepository: SecurityRepository
) {
    // Always use dark theme styling since we force dark theme
    val textColor = Color.White
    var checkResult by remember { mutableStateOf<FileCheckResult?>(null) }
    var isChecking by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "URL/File Checker",
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            FileCheckerModule(
                onFileCheck = { request ->
                    isChecking = true
                    checkResult = null
                    scope.launch {
                        try {
                            val result = securityRepository.checkFileOrUrl(request)
                            checkResult = result
                        } catch (e: Exception) {
                            // Handle error
                        } finally {
                            isChecking = false
                        }
                    }
                },
                checkResult = checkResult,
                isLoading = isChecking,
                onDismissResult = { checkResult = null },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}