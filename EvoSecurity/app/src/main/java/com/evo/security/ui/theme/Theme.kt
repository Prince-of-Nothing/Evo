package com.evo.security.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = EvoBlueLight,
    secondary = PurpleGrey80,
    tertiary = Pink80,
    background = DarkBackground,
    surface = DarkBackground
)

private val LightColorScheme = lightColorScheme(
    primary = EvoBlue,
    secondary = PurpleGrey40,
    tertiary = Pink40,
    background = DarkBackground,
    surface = DarkBackground

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

@Composable
fun EvoSecurityTheme(
    darkTheme: Boolean = true, // Force dark theme always
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false, // Disable dynamic color to use our custom theme
    content: @Composable () -> Unit
) {
    val colorScheme = DarkColorScheme // Always use dark color scheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}