package com.goggins.discipline.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily

private val DarkScheme = darkColorScheme(
    primary = NothingWhite,
    onPrimary = NothingBlack,
    secondary = NothingRed,
    background = NothingBlack,
    onBackground = NothingWhite,
    surface = NothingSurface,
    onSurface = NothingWhite,
    surfaceVariant = NothingLine,
    onSurfaceVariant = NothingGrey,
    error = NothingRed
)

private val LightScheme = lightColorScheme(
    primary = NothingBlack,
    onPrimary = NothingWhite,
    secondary = NothingRed,
    background = NothingPaper,
    onBackground = NothingBlack,
    surface = NothingWhite,
    onSurface = NothingBlack,
    surfaceVariant = NothingGrey,
    onSurfaceVariant = NothingBlack,
    error = NothingRed
)

val Mono = FontFamily.Monospace

@Composable
fun GogginsTheme(dark: Boolean = true, content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (dark) DarkScheme else LightScheme,
        typography = MaterialTheme.typography,
        content = content
    )
}
