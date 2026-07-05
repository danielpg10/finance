package com.example.finance.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = EmeraldDark,
    onPrimary = SurfaceLight,
    primaryContainer = EmeraldContainer,
    onPrimaryContainer = OnEmeraldContainer,
    secondary = InkSoft,
    onSecondary = SurfaceLight,
    secondaryContainer = EmeraldContainer,
    onSecondaryContainer = OnEmeraldContainer,
    tertiary = SavingBlue,
    onTertiary = SurfaceLight,
    tertiaryContainer = Color(0xFFDBEAFE),
    onTertiaryContainer = Color(0xFF1E3A8A),
    background = Mist,
    onBackground = Ink,
    surface = SurfaceLight,
    onSurface = Ink,
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = InkSoft,
    error = ExpenseRed,
    outline = Color(0xFFCBD5D1)
)

private val DarkColorScheme = darkColorScheme(
    primary = EmeraldLight,
    onPrimary = NightBackground,
    primaryContainer = Color(0xFF0E3D2E),
    onPrimaryContainer = EmeraldContainer,
    secondary = NightTextSoft,
    onSecondary = NightBackground,
    secondaryContainer = Color(0xFF0E3D2E),
    onSecondaryContainer = EmeraldContainer,
    tertiary = Color(0xFF93C5FD),
    onTertiary = NightBackground,
    tertiaryContainer = Color(0xFF1E3A5F),
    onTertiaryContainer = Color(0xFFDBEAFE),
    background = NightBackground,
    onBackground = NightText,
    surface = NightSurface,
    onSurface = NightText,
    surfaceVariant = NightSurfaceVariant,
    onSurfaceVariant = NightTextSoft,
    error = Color(0xFFF87171),
    outline = Color(0xFF3A4A43)
)

@Composable
fun FinanceTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        typography = Typography,
        content = content
    )
}
