package com.evo.security.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.ui.graphics.Color
import com.evo.security.ui.theme.EvoBlue
import com.evo.security.ui.theme.DarkCardBackground
import com.evo.security.ui.theme.LightCardBackground
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.evo.security.model.FileCheckRequest
import com.evo.security.model.FileCheckResult
import com.evo.security.service.VirusTotalService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException

@Composable
fun FileCheckerModule(
    onFileCheck: (FileCheckRequest) -> Unit,
    checkResult: FileCheckResult?,
    isLoading: Boolean = false,
    onDismissResult: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var urlInput by remember { mutableStateOf("") }
    var selectedFileUri by remember { mutableStateOf<Uri?>(null) }
    var lastCheckedUrl by remember { mutableStateOf<String?>(null) }

    val filePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedFileUri = uri
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // URL Input Section
        OutlinedTextField(
            value = urlInput,
            onValueChange = { urlInput = it },
            placeholder = {
                Text(
                    "URL...",
                    color = Color.White.copy(alpha = 0.6f)
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp)),
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedBorderColor = EvoBlue,
                unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                cursorColor = EvoBlue
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = {
                    if (urlInput.isNotBlank()) {
                        val urlToCheck = urlInput.trim()
                        lastCheckedUrl = urlToCheck
                        onFileCheck(FileCheckRequest(url = urlToCheck))
                    }
                },
                enabled = urlInput.isNotBlank() && !isLoading,
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 20.dp, horizontal = 0.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = EvoBlue,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Check URL")
            }

            Button(
                onClick = { filePicker.launch("*/*") },
                enabled = !isLoading,
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 20.dp, horizontal = 0.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFE8F0FB),
                    contentColor = Color.Black
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Upload File")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

            // Selected file display
            selectedFileUri?.let { uri ->
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Selected: ${uri.lastPathSegment ?: "Unknown file"}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = {
                            selectedFileUri?.let { fileUri ->
                                scope.launch {
                                    try {
                                        // Compute file hash
                                        val fileHash = withContext(Dispatchers.IO) {
                                            computeFileHash(context, fileUri)
                                        }

                                        if (fileHash != null) {
                                            onFileCheck(
                                                FileCheckRequest(
                                                    fileName = fileUri.lastPathSegment,
                                                    fileUri = fileUri.toString(),
                                                    fileHash = fileHash
                                                )
                                            )
                                        } else {
                                            Log.e("FileChecker", "Failed to compute file hash")
                                        }
                                    } catch (e: Exception) {
                                        Log.e("FileChecker", "Error computing file hash", e)
                                    }
                                }
                            }
                        },
                        enabled = !isLoading,
                        modifier = Modifier
                            .weight(1f)
                            .padding(vertical = 20.dp, horizontal = 0.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = EvoBlue,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text("Validate File")
                    }

                    Button(
                        onClick = {
                            selectedFileUri = null
                        },
                        enabled = !isLoading,
                        modifier = Modifier
                            .weight(1f)
                            .padding(vertical = 20.dp, horizontal = 0.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFE53E3E),
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Remove",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Remove")
                    }
                }
            }

        // Loading popup
        if (isLoading) {
            Dialog(
                onDismissRequest = { },
                properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
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
                            "Verifying...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White
                        )
                    }
                }
            }
        }

        // Results popup
        checkResult?.let { result ->
            Dialog(onDismissRequest = onDismissResult) {
                CheckResultPopup(
                    result = result,
                    request = if (lastCheckedUrl != null) {
                        FileCheckRequest(url = lastCheckedUrl)
                    } else if (selectedFileUri != null) {
                        FileCheckRequest(
                            fileName = selectedFileUri?.lastPathSegment,
                            fileUri = selectedFileUri.toString()
                        )
                    } else null,
                    onDismiss = onDismissResult
                )
            }
        }
    }
}

@Composable
private fun CheckResultPopup(
    result: FileCheckResult,
    request: FileCheckRequest?,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    Card(
        colors = CardDefaults.cardColors(
            containerColor = DarkCardBackground
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            Text(
                text = if (result.isSafe) {
                    if (request?.fileHash != null || request?.fileUri != null) {
                        "This file is safe"
                    } else {
                        "This link is safe"
                    }
                } else {
                    "Threat Detected"
                },
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.fillMaxWidth()
            )

            if (result.threats.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Threats found:",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = Color.White,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                result.threats.forEach { threat ->
                    Text(
                        text = "• $threat",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White,
                        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)
                    )
                }
            }


            Spacer(modifier = Modifier.height(16.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Show appropriate action button based on result and request type
                if (result.isSafe && request?.url != null && request.url.isNotBlank() &&
                    request.fileHash == null && request.fileUri == null) {
                    Button(
                        onClick = {
                            try {
                                android.util.Log.d("FileChecker", "Attempting to open URL: ${request.url}")

                                // Ensure URL has proper protocol
                                val urlToOpen = if (!request.url.startsWith("http://") && !request.url.startsWith("https://")) {
                                    "https://${request.url}"
                                } else {
                                    request.url
                                }

                                android.util.Log.d("FileChecker", "Opening URL: $urlToOpen")
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(urlToOpen))
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                context.startActivity(intent)
                                onDismiss()
                            } catch (e: Exception) {
                                android.util.Log.e("FileChecker", "Error opening URL: ${request.url}", e)

                                // Try alternative method
                                try {
                                    val chooserIntent = Intent.createChooser(
                                        Intent(Intent.ACTION_VIEW, Uri.parse(request.url)),
                                        "Open with"
                                    )
                                    chooserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    context.startActivity(chooserIntent)
                                    onDismiss()
                                } catch (e2: Exception) {
                                    android.util.Log.e("FileChecker", "Alternative method also failed", e2)
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = EvoBlue,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Browser")
                    }
                } else if (!result.isSafe && request?.fileUri != null) {
                    Button(
                        onClick = {
                            // TODO: Implement file deletion
                            onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFE53E3E),
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Delete File")
                    }
                }

                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = EvoBlue,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("OK")
                }
            }
        }
    }
}

private suspend fun computeFileHash(context: Context, uri: Uri): String? = withContext(Dispatchers.IO) {
    try {
        context.contentResolver.openInputStream(uri)?.use { inputStream ->
            val bytes = inputStream.readBytes()
            val virusTotalService = VirusTotalService(context)
            virusTotalService.generateFileHash(bytes)
        }
    } catch (e: IOException) {
        Log.e("FileChecker", "Error reading file for hash computation", e)
        null
    } catch (e: Exception) {
        Log.e("FileChecker", "Unexpected error during hash computation", e)
        null
    }
}