package com.seduligma.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Color(0xFF135BEC),
    secondary = Color(0xFF4059AD),
    tertiary = Color(0xFF006E5A),
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFFB6C4FF),
    secondary = Color(0xFFB8C3FF),
    tertiary = Color(0xFF8CE8CD),
)

@Composable
fun SeduligmaTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColors,
        typography = androidx.compose.material3.Typography(),
        content = content,
    )
}
