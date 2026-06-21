package com.example.presentation.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = ElectricBlue,
    onPrimary = WhiteText,
    primaryContainer = DarkSurfaceVariant,
    onPrimaryContainer = WhiteText,
    secondary = GrayText,
    onSecondary = DarkBackground,
    background = DarkBackground,
    onBackground = WhiteText,
    surface = DarkSurface,
    onSurface = WhiteText,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = WhiteText,
    outline = GlassBorder,
    error = Color(0xFFEF4444)
)

private val LightColorScheme = lightColorScheme(
    primary = ElectricBlue,
    onPrimary = WhiteText,
    primaryContainer = LightSurfaceVariant,
    onPrimaryContainer = DarkText,
    secondary = MutedText,
    onSecondary = LightBackground,
    background = LightBackground,
    onBackground = DarkText,
    surface = LightSurface,
    onSurface = DarkText,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = DarkText,
    outline = Color(0x1A000000),
    error = Color(0xFFDC2626)
)

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = true, // Forcing dark theme for premium tech aesthetic
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

  val view = LocalView.current
  if (!view.isInEditMode) {
    SideEffect {
      val window = (view.context as Activity).window
      window.statusBarColor = colorScheme.background.toArgb()
      WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
    }
  }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
