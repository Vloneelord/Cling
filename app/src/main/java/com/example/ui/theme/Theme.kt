package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

// Unified Sahara Night Dark Color Scheme (Enforced globally)
private val SaharaNightColorScheme = darkColorScheme(
    primary = AccentBlue,
    onPrimary = PrimaryText,
    primaryContainer = CardSurfaceVariant,
    onPrimaryContainer = PrimaryText,
    secondary = AccentAmber,
    onSecondary = AppBackground,
    secondaryContainer = CardSurfaceVariant,
    onSecondaryContainer = PrimaryText,
    tertiary = AccentEmerald,
    onTertiary = PrimaryText,
    background = AppBackground,
    onBackground = PrimaryText,
    surface = CardSurface,
    onSurface = PrimaryText,
    surfaceVariant = CardSurfaceVariant,
    onSurfaceVariant = SecondaryText,
    outline = BorderStroke
)

@Composable
fun GuelmimExpressTheme(
    darkTheme: Boolean = true, // Locked to true for Sahara Night Dark Theme
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = SaharaNightColorScheme,
        typography = Typography,
        shapes = androidx.compose.material3.Shapes()
    ) {
        // Enforce PrimaryText as default content color so text never resolves to dark grey/black
        CompositionLocalProvider(
            LocalContentColor provides PrimaryText,
            content = content
        )
    }
}
