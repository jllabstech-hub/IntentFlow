package com.intentflow.ui.theme

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
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// ─── Dark Color Scheme ────────────────────────────────────────────────────────

private val DarkColorScheme = darkColorScheme(
    primary = IntentPrimary,
    onPrimary = IntentOnPrimary,
    primaryContainer = IntentPrimaryContainer,
    onPrimaryContainer = IntentOnPrimaryContainer,
    secondary = IntentSecondary,
    onSecondary = IntentOnSecondary,
    secondaryContainer = IntentSecondaryContainer,
    onSecondaryContainer = IntentOnSecondaryContainer,
    tertiary = IntentTertiary,
    onTertiary = IntentOnTertiary,
    tertiaryContainer = IntentTertiaryContainer,
    onTertiaryContainer = IntentOnTertiaryContainer,
    error = IntentError,
    onError = IntentOnError,
    errorContainer = IntentErrorContainer,
    onErrorContainer = IntentOnErrorContainer,
    background = IntentBackground,
    onBackground = IntentOnBackground,
    surface = IntentSurface,
    onSurface = IntentOnSurface,
    surfaceVariant = IntentSurfaceVariant,
    onSurfaceVariant = IntentOnSurfaceVariant,
    outline = IntentOutline,
    outlineVariant = IntentOutlineVariant
)

// ─── Light Color Scheme ───────────────────────────────────────────────────────

private val LightColorScheme = lightColorScheme(
    primary = IntentPrimary,
    onPrimary = IntentOnPrimary,
    primaryContainer = IntentPrimaryContainer,
    onPrimaryContainer = IntentOnPrimaryContainer,
    secondary = IntentSecondary,
    onSecondary = IntentOnSecondary,
    secondaryContainer = IntentSecondaryContainer,
    onSecondaryContainer = IntentOnSecondaryContainer,
    tertiary = IntentTertiary,
    onTertiary = IntentOnTertiary,
    tertiaryContainer = IntentTertiaryContainer,
    onTertiaryContainer = IntentOnTertiaryContainer,
    error = IntentError,
    onError = IntentOnError,
    errorContainer = IntentErrorContainer,
    onErrorContainer = IntentOnErrorContainer,
    background = IntentBackgroundLight,
    onBackground = IntentOnBackgroundLight,
    surface = IntentSurfaceLight,
    onSurface = IntentOnSurfaceLight,
    surfaceVariant = IntentSurfaceVariantLight,
    onSurfaceVariant = IntentOnSurfaceVariantLight,
    outline = IntentOutlineLight,
    outlineVariant = IntentOutlineVariantLight
)

// ─── Theme Composable ─────────────────────────────────────────────────────────

/**
 * IntentFlow Material 3 Compose theme.
 *
 * Supports:
 * - Dark and light color schemes
 * - Android 12+ dynamic color (Material You) — opt-in via [dynamicColor]
 * - Status bar color update matching the surface color
 *
 * @param darkTheme Whether to apply the dark color scheme. Defaults to system setting.
 * @param dynamicColor Enable Material You dynamic colors on Android 12+. Defaults to false
 *   to preserve IntentFlow brand identity.
 * @param content The composable content to render inside the theme.
 */
@Composable
fun IntentFlowTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
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

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = IntentFlowTypography,
        content = content
    )
}
