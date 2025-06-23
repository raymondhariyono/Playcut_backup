package com.raymondHariyono.playcut.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable

@Composable
fun PlayCUtTheme(
    useDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (useDarkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colors,
        typography = AppTypography,
        content = content
    )
}

private val LightColorScheme = lightColorScheme(
    primary = PrimaryDarkBlue,
    onPrimary = White,
    secondary = SecondaryLightBlue,
    onSecondary = Black,
    background = White,
    onBackground = Black,
    surface = White,
    onSurface = PrimaryDarkBlue,
    error = TertiaryRed,
    onError = White
)

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryDarkBlue,
    onPrimary = White,
    secondary = SecondaryLightBlue,
    onSecondary = Black,
    background = Black,
    onBackground = White,
    surface = PrimaryDarkBlue,
    onSurface = White,
    error = TertiaryRed,
    onError = White
)
