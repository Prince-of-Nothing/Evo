package com.evo.security

import BlockedLinkDialogComposable
import BrowserChooserDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.runtime.DisposableEffect
import com.evo.security.ui.theme.EvoBlue
import com.evo.security.ui.theme.DarkCardBackground
import com.evo.security.ui.theme.EvoSecurityTheme
import kotlinx.coroutines.*
import androidx.core.net.toUri
import com.evo.security.service.VirusTotalService

class LinkInterceptorActivity : AppCompatActivity() {
    private val scope = CoroutineScope(Dispatchers.IO)
    private lateinit var virusTotalService: VirusTotalService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        virusTotalService = VirusTotalService(this)
        val url = intent?.dataString

        Log.d("EVO-Security", "Intercepted URL: $url")

        if (url == null) {
            finish()
            return
        }

        setContent {
            EvoSecurityTheme {
                var isLoading by remember { mutableStateOf(true) }
                var isSafe by remember { mutableStateOf<Boolean?>(null) }

                LaunchedEffect(url) {
                    val safe = validateWithService(url)
                    isSafe = safe
                    isLoading = false
                }

                when {
                    isLoading -> {
                        ShowLoadingDialog(url)
                    }
                    isSafe == true -> {
                        OpenInUserBrowser(url)
                    }
                    else -> {
                        ShowBlockScreen(url)
                    }
                }
            }
        }
    }

    private suspend fun validateWithService(url: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                Log.d("EVO-Security", "Validating URL with security API: $url")
                val response = virusTotalService.checkUrl(url)

                if (response != null) {
                    val isSafe = response.isSafe()
                    val threatLevel = response.getThreatLevel()
                    Log.d("EVO-Security", "API Response - Safe: $isSafe, Threat Level: $threatLevel")
                    Log.d("EVO-Security", "Malicious: ${response.malicious}, Suspicious: ${response.suspicious}")
                    isSafe
                } else {
                    Log.w("EVO-Security", "API call failed, defaulting to unsafe")
                    // If API call fails, default to unsafe for security
                    false
                }
            } catch (e: Exception) {
                Log.e("EVO-Security", "Error validating URL", e)
                false
            }
        }
    }

    @Composable
    private fun ShowLoadingDialog(url: String) {
        val backDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher

        DisposableEffect(Unit) {
            val callback = object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    finish()
                }
            }
            backDispatcher?.addCallback(callback)
            onDispose {
                callback.remove()
            }
        }

        Dialog(
            onDismissRequest = { finish() }
        ) {
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
                    CircularProgressIndicator(
                        color = EvoBlue,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "Verifying URL...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }

    @Composable
    private fun OpenInUserBrowser(url: String) {
        var context = LocalContext.current

        val pm = context.packageManager
        val testUrl = Uri.parse("http://www.google.com") // nu folosi schema custom
        val intent = Intent(Intent.ACTION_VIEW, testUrl).apply {
            addCategory(Intent.CATEGORY_BROWSABLE)
        }

        val browsers = pm.queryIntentActivities(intent, PackageManager.MATCH_ALL)

        for (resolveInfo in browsers) {
            Log.d("EVO-Security", "Browser found: ${resolveInfo.activityInfo.packageName}")
        }

        for (info in browsers) {
            val appName = info.loadLabel(pm).toString()
            val appIcon = info.loadIcon(pm)
            val packageName = info.activityInfo.packageName
            Log.d("Browsers", "Found browser: $appName ($packageName)")
        }

        BrowserChooserDialog(browsers, url, context) { }
    }

    @Composable
    private fun ShowBlockScreen(url: String) {
        val backDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher

        DisposableEffect(Unit) {
            val callback = object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    finish()
                }
            }
            backDispatcher?.addCallback(callback)
            onDispose {
                callback.remove()
            }
        }

        Dialog(
            onDismissRequest = { finish() }
        ) {
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
                        "⚠️ Threat Detected",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        "This URL has been flagged as potentially dangerous and has been blocked for your safety.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = { finish() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = EvoBlue,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Go Back")
                    }
                }
            }
        }
    }
}