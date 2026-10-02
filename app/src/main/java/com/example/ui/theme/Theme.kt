package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = SkyPrimaryCyan,
    onPrimary = Color(0xFF003544),
    primaryContainer = Color(0xFF004D63),
    onPrimaryContainer = Color(0xFFB8EAFF),
    secondary = SkySecondaryBlue,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF1B2E4B),
    onSecondaryContainer = Color(0xFFD6E4FF),
    tertiary = SkyTertiaryGold,
    onTertiary = Color(0xFF452B00),
    background = SkyDarkBackground,
    onBackground = FrostWhite,
    surface = SkyDarkSurface,
    onSurface = FrostWhite,
    surfaceVariant = SkyDarkSurfaceVariant,
    onSurfaceVariant = Color(0xFF94A3B8),
    outline = GlassBorderDark
)

private val LightColorScheme = lightColorScheme(
    primary = SkyPrimaryDark,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD0E8FF),
    onPrimaryContainer = Color(0xFF001E2B),
    secondary = SkySecondaryBlue,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE0EDFF),
    onSecondaryContainer = Color(0xFF001A41),
    tertiary = SkySolarAmber,
    onTertiary = Color.White,
    background = SkyLightBackground,
    onBackground = Color(0xFF0F172A),
    surface = SkyLightSurface,
    onSurface = Color(0xFF0F172A),
    surfaceVariant = SkyLightSurfaceVariant,
    onSurfaceVariant = Color(0xFF475569),
    outline = Color(0xFFCBD5E1)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Default to sleek atmospheric dark theme for weather app
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
