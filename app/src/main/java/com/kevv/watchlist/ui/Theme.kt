package com.kevv.watchlist.ui

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val Light = lightColorScheme(
    primary = Color(0xFF3B4A6B), secondary = Color(0xFF5C6B8A),
    background = Color(0xFFF7F8FA), surface = Color(0xFFFFFFFF),
    surfaceVariant = Color(0xFFE9ECF2)
)
private val Dark = darkColorScheme(
    primary = Color(0xFFAFC2F0), secondary = Color(0xFF9AA8C8),
    background = Color(0xFF111318), surface = Color(0xFF1A1D24),
    surfaceVariant = Color(0xFF2A2F3A)
)

@Composable
fun KevvTheme(content: @Composable () -> Unit) {
    val dark = isSystemInDarkTheme()
    val ctx = LocalContext.current
    val scheme = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ->
            if (dark) dynamicDarkColorScheme(ctx) else dynamicLightColorScheme(ctx)
        dark -> Dark
        else -> Light
    }
    MaterialTheme(colorScheme = scheme, content = content)
}
