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

private val DarkColorScheme =
  darkColorScheme(
    primary = VibrantDarkPrimary,
    onPrimary = VibrantDarkOnPrimary,
    primaryContainer = VibrantDarkPrimaryContainer,
    onPrimaryContainer = VibrantDarkOnPrimaryContainer,
    secondary = VibrantDarkSecondary,
    secondaryContainer = VibrantDarkSecondaryContainer,
    background = VibrantDarkBackground,
    surface = VibrantDarkSurface,
    onBackground = VibrantDarkOnBackground,
    onSurface = VibrantDarkOnSurface,
    surfaceVariant = VibrantDarkSurfaceVariant,
    outline = VibrantDarkOutline,
    error = VibrantDarkError
  )

private val LightColorScheme =
  lightColorScheme(
    primary = VibrantPrimary,
    onPrimary = VibrantOnPrimary,
    primaryContainer = VibrantPrimaryContainer,
    onPrimaryContainer = VibrantOnPrimaryContainer,
    secondary = VibrantSecondary,
    secondaryContainer = VibrantSecondaryContainer,
    onSecondary = VibrantOnSecondary,
    tertiary = VibrantTertiary,
    background = VibrantBackground,
    surface = VibrantSurface,
    onBackground = VibrantOnBackground,
    onSurface = VibrantOnSurface,
    surfaceVariant = VibrantSurfaceVariant,
    outline = VibrantOutline,
    error = VibrantError
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  // Force false by default to retain the custom-tailored brand identity
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }

      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
