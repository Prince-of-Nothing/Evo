package com.evo.security

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.evo.security.model.FileCheckRequest
import com.evo.security.model.FileCheckResult
import com.evo.security.model.SecurityNews
import com.evo.security.repository.SecurityRepository
import com.evo.security.service.NotificationService
import com.evo.security.ui.screens.HomeScreen
import com.evo.security.ui.screens.UrlCheckerScreen
import com.evo.security.ui.screens.NewsFeedScreen
import com.evo.security.ui.theme.EvoSecurityTheme
import com.evo.security.ui.theme.EvoBlue
import com.evo.security.ui.theme.DarkCardBackground
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private lateinit var securityRepository: SecurityRepository
    private lateinit var notificationService: NotificationService

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        // Handle permission result if needed
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        securityRepository = SecurityRepository(this)
        notificationService = NotificationService(this)

        // Request notification permission for Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        // Load RSS news on startup
        lifecycleScope.launch {
            securityRepository.loadRssNews()
        }

        // Set up periodic news checking (simulate real-time updates)
        lifecycleScope.launch {
            // Listen for new security news and send notifications
            securityRepository.securityNews.collect { newsList ->
                // For demo purposes, notify about high/critical severity news
                newsList.filter {
                    it.severity in listOf(com.evo.security.model.SecuritySeverity.HIGH, com.evo.security.model.SecuritySeverity.CRITICAL)
                }.forEach { news ->
                    notificationService.showSecurityNewsNotification(news)
                }
            }
        }

        // Disable edge-to-edge to prevent white background issues
        // enableEdgeToEdge()
        setContent {
            EvoSecurityTheme {
                var showDefaultBrowserDialog by remember { mutableStateOf(false) }

                LaunchedEffect(Unit) {
                    if (!isDefaultBrowser()) {
                        showDefaultBrowserDialog = true
                    }
                }

                MainScreen(
                    securityRepository = securityRepository,
                    modifier = Modifier.fillMaxSize()
                )

                if (showDefaultBrowserDialog) {
                    DefaultBrowserDialog(
                        onDismiss = { showDefaultBrowserDialog = false },
                        onSetAsDefault = {
                            openDefaultBrowserSettings()
                            showDefaultBrowserDialog = false
                        }
                    )
                }
            }
        }
    }

    private fun isDefaultBrowser(): Boolean {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("http://www.example.com"))
        val resolveInfo = packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY)
        return resolveInfo?.activityInfo?.packageName == packageName
    }

    private fun openDefaultBrowserSettings() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Android 10+ - Open default apps settings
            val intent = Intent(Settings.ACTION_MANAGE_DEFAULT_APPS_SETTINGS)
            startActivity(intent)
        } else {
            // Older Android versions - Open app info settings
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            intent.data = Uri.parse("package:$packageName")
            startActivity(intent)
        }
    }
}

@Composable
fun DefaultBrowserDialog(
    onDismiss: () -> Unit,
    onSetAsDefault: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = DarkCardBackground
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "🛡️ Set as Default Browser",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    "To protect you from malicious links, Evo Security needs to be your default browser. This allows us to scan all links before you visit them.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Later")
                    }

                    Button(
                        onClick = onSetAsDefault,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = EvoBlue,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Set Default")
                    }
                }
            }
        }
    }
}

@Composable
fun MainScreen(
    securityRepository: SecurityRepository,
    modifier: Modifier = Modifier
) {
    val securityNews by securityRepository.securityNews.collectAsState()
    var currentScreen by remember { mutableStateOf("home") }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF1E1E1E))
    ) {
        when (currentScreen) {
            "home" -> {
                HomeScreen(
                    onUrlCheckerClick = { currentScreen = "urlchecker" },
                    onNewsFeedClick = { currentScreen = "newsfeed" }
                )
            }
            "urlchecker" -> {
                UrlCheckerScreen(
                    onBack = { currentScreen = "home" },
                    securityRepository = securityRepository
                )
            }
            "newsfeed" -> {
                NewsFeedScreen(
                    onBack = { currentScreen = "home" },
                    securityNews = securityNews
                )
            }
        }
    }
}