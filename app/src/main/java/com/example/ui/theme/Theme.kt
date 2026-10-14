package com.example.ui.theme

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
    primary = TerracottaPrimaryDark,
    onPrimary = Color.Black,
    primaryContainer = Color(0xFF6B2711),
    onPrimaryContainer = Color(0xFFFFDBCF),
    secondary = SaharaAmberDark,
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF6B3C00),
    onSecondaryContainer = Color(0xFFFFE0BD),
    tertiary = OasisGreenDark,
    onTertiary = Color.Black,
    tertiaryContainer = Color(0xFF134E33),
    onTertiaryContainer = Color(0xFFD1F0DE),
    background = NightSlateDark,
    onBackground = Color(0xFFEDE0DA),
    surface = NightSlateSurface,
    onSurface = Color(0xFFEDE0DA),
    surfaceVariant = NightSlateSurfaceVariant,
    onSurfaceVariant = Color(0xFFD4C3B9),
    outline = DesertSandOutline
)

private val LightColorScheme = lightColorScheme(
    primary = TerracottaPrimary,
    onPrimary = Color.White,
    primaryContainer = TerracottaPrimaryContainer,
    onPrimaryContainer = TerracottaOnPrimaryContainer,
    secondary = SaharaAmber,
    onSecondary = Color.White,
    secondaryContainer = SaharaAmberContainer,
    onSecondaryContainer = Color(0xFF2C1600),
    tertiary = OasisGreen,
    onTertiary = Color.White,
    tertiaryContainer = OasisGreenContainer,
    onTertiaryContainer = Color(0xFF032114),
    background = DesertSandLight,
    onBackground = Color(0xFF241A15),
    surface = DesertSandSurface,
    onSurface = Color(0xFF241A15),
    surfaceVariant = DesertSandSurfaceVariant,
    onSurfaceVariant = Color(0xFF55443B),
    outline = DesertSandOutline
)

@Composable
fun GuelmimExpressTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep authentic Guelmim Sahara theme active by default
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
