package com.example.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = GeoAmberLight,
    onPrimary = GeoBackground,
    primaryContainer = GeoSurface,
    onPrimaryContainer = GeoAmberLight,
    secondary = GeoAmber,
    onSecondary = GeoBackground,
    secondaryContainer = GeoOrange,
    onSecondaryContainer = GeoTextPrimary,
    tertiary = GeoGold,
    onTertiary = GeoBackground,
    background = GeoBackground,
    onBackground = GeoTextPrimary,
    surface = GeoSurface,
    onSurface = GeoTextPrimary,
    surfaceVariant = GeoGlassBg,
    onSurfaceVariant = GeoTextPrimary,
    outline = GeoGlassBorder
)

private val LightColorScheme = lightColorScheme(
    primary = GeoOrange,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE0F7FA), // Light Cyan
    onPrimaryContainer = Color(0xFF006064), // Dark Cyan
    secondary = GeoAmber,
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFFFFF9C4), // Light Yellow
    onSecondaryContainer = Color(0xFFF57F17),
    tertiary = GeoGold,
    onTertiary = Color.Black,
    background = Color(0xFFF5F5F6), // Very light gray
    onBackground = Color(0xFF1E1E1E), // Dark Gray
    surface = Color.White,
    onSurface = Color(0xFF1E1E1E),
    surfaceVariant = Color(0x11000000), // Light Glass
    onSurfaceVariant = Color(0xFF1E1E1E),
    outline = Color(0x33000000)
)

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false, // Keep dynamic false to preserve custom colors
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
