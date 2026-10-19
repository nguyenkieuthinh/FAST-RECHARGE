package com.thinhnk.fastapp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Color(0xFF047857),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFDCEFE6),
    onPrimaryContainer = Color(0xFF0B1F1A),
    secondary = Color(0xFF2563EB),
    onSecondary = Color.White,
    background = Color(0xFFF4F7F5),
    onBackground = Color(0xFF0B1F1A),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF0B1F1A),
    surfaceVariant = Color(0xFFEAF0EC),
    onSurfaceVariant = Color(0xFF4A5E57),
    outline = Color(0xFFD6E2DC),
    error = Color(0xFFA61B1B),
    onError = Color.White,
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF3DDC97),
    onPrimary = Color(0xFF062018),
    primaryContainer = Color(0xFF123529),
    onPrimaryContainer = Color(0xFFE9F2ED),
    secondary = Color(0xFF8FB1FF),
    onSecondary = Color(0xFF0A1530),
    background = Color(0xFF0B1512),
    onBackground = Color(0xFFE9F2ED),
    surface = Color(0xFF11201B),
    onSurface = Color(0xFFE9F2ED),
    surfaceVariant = Color(0xFF182C24),
    onSurfaceVariant = Color(0xFFA2B7AD),
    outline = Color(0xFF294239),
    error = Color(0xFFF58A8A),
    onError = Color(0xFF3A0A0A),
)

@Composable
fun FastTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        content = content,
    )
}
