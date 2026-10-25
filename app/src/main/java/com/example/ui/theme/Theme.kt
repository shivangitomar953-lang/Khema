package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
  primary = PrimaryIndigoLight,
  onPrimary = Color.White,
  primaryContainer = Color(0xFF312E81),
  onPrimaryContainer = Color(0xFFE0E7FF),
  secondary = AccentAmber,
  onSecondary = Color.Black,
  secondaryContainer = Color(0xFF78350F),
  onSecondaryContainer = Color(0xFFFEF3C7),
  tertiary = AccentOrange,
  background = StudioDarkBg,
  onBackground = TextWhite,
  surface = StudioDarkSurface,
  onSurface = TextWhite,
  surfaceVariant = StudioDarkSurfaceElevated,
  onSurfaceVariant = TextGray,
  outline = StudioDarkBorder
)

private val LightColorScheme = lightColorScheme(
  primary = PrimaryIndigo,
  onPrimary = Color.White,
  primaryContainer = Color(0xFFEEF2FF),
  onPrimaryContainer = Color(0xFF312E81),
  secondary = AccentAmber,
  onSecondary = Color.Black,
  background = Color(0xFFF8FAFC),
  onBackground = TextDark,
  surface = Color.White,
  onSurface = TextDark,
  surfaceVariant = Color(0xFFF1F5F9),
  onSurfaceVariant = Color(0xFF64748B),
  outline = Color(0xFFCBD5E1)
)

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = true, // Default to sleek creative studio dark theme
  content: @Composable () -> Unit
) {
  val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}
