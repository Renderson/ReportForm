package com.rendersoncs.report.ui.theme

import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView

private val LightColorScheme = lightColorScheme(
    primary = Navy,
    onPrimary = Color.White,
    primaryContainer = NavyContainer,
    onPrimaryContainer = OnNavyContainer,
    secondary = Blue,
    onSecondary = Color.White,
    secondaryContainer = BlueContainer,
    onSecondaryContainer = OnBlueContainer,
    tertiary = Brown,
    onTertiary = Color.White,
    tertiaryContainer = BrownContainer,
    onTertiaryContainer = OnBrownContainer,
    background = Canvas,
    onBackground = OnSurface,
    surface = SurfaceWhite,
    onSurface = OnSurface,
    surfaceVariant = NavyContainer,
    onSurfaceVariant = OnSurfaceMuted,
    outline = Outline,
    outlineVariant = OutlineVariant,
    error = ErrorRed,
    onError = OnError,
    errorContainer = ErrorContainer,
    onErrorContainer = NavyDark
)

private val DarkColorScheme = darkColorScheme(
    primary = NavyLight,
    onPrimary = NavyDark,
    primaryContainer = Navy,
    onPrimaryContainer = NavyLight,
    secondary = BlueLight,
    onSecondary = NavyDark,
    secondaryContainer = BlueDark,
    onSecondaryContainer = BlueLight,
    tertiary = BrownLight,
    onTertiary = BrownDark,
    tertiaryContainer = Brown,
    onTertiaryContainer = BrownLight,
    background = CanvasDark,
    onBackground = OnDark,
    surface = SurfaceDark,
    onSurface = OnDark,
    surfaceVariant = Color(0xFF243B53),
    onSurfaceVariant = NavyLight,
    outline = OutlineDark,
    outlineVariant = Color(0xFF243B53),
    error = ErrorRed,
    onError = OnError
)

@Composable
fun ReportTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    adjustSystemBars: Boolean = true,
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    if (!view.isInEditMode && adjustSystemBars) {
        DisposableEffect(darkTheme) {
            val activity = view.context as ComponentActivity
            val transparent = Color.Transparent.toArgb()
            activity.enableEdgeToEdge(
                statusBarStyle = SystemBarStyle.auto(transparent, transparent) { darkTheme },
                navigationBarStyle = SystemBarStyle.auto(transparent, transparent) { darkTheme }
            )
            onDispose { }
        }
    }
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        typography = ReportTypography,
        shapes = ReportShapes,
        content = content
    )
}
