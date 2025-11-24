package com.example.bodega_flow.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = FlowBlue,
    onPrimary = Color.White,
    primaryContainer = FlowBlueLight,
    onPrimaryContainer = FlowTextMain,

    secondary = FlowAccentGreen,
    onSecondary = Color.White,

    background = Color.White,
    onBackground = FlowTextMain,

    surface = Color.White,
    onSurface = FlowTextMain,

    error = FlowErrorRed,
    onError = Color.White,
)

private val DarkColors = darkColorScheme(
    primary = FlowBlueLight,
    onPrimary = Color.Black,
    secondary = FlowAccentGreen,
    background = Color(0xFF121212),
    onBackground = Color.White,
    surface = Color(0xFF1E1E1E),
    onSurface = Color.White,
    error = FlowErrorRed,
    onError = Color.Black
)

@Composable
fun BodegaFlowTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme =
        if (darkTheme) DarkColors
        else LightColors

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
