package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val HolographicColorScheme = darkColorScheme(
    primary = CyanCore,
    onPrimary = DeepSpace,
    primaryContainer = HoloSurfaceVariant,
    onPrimaryContainer = CyanCore,
    secondary = CyanAccent,
    onSecondary = DeepSpace,
    secondaryContainer = HoloSurface,
    onSecondaryContainer = CyanAccent,
    tertiary = NeonIndigo,
    onTertiary = TextPrimary,
    background = DeepSpace,
    onBackground = TextPrimary,
    surface = HoloSurface,
    onSurface = TextPrimary,
    surfaceVariant = HoloSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    outline = HoloBorder,
    outlineVariant = HoloBorderGlow
)

@Composable
fun LargeControllerTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = HolographicColorScheme,
        typography = Typography,
        content = content
    )
}
