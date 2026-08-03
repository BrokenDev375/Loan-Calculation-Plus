package com.loancaculator.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val Primary = Color(0xFF0B2E4F)
val PrimaryDark = Color(0xFF123C64)
val Secondary = Color(0xFF178A8A)
val Gold = Color(0xFFE7A93C)
val OnPrimary = Color.White
val BgLight = Color(0xFFD0EFFF)
val BgDark = Color(0xFF101418)
val TextPrimary = Color(0xFF10233A)

private val LightColors = lightColorScheme(
    primary = Primary,
    onPrimary = OnPrimary,
    secondary = Secondary,
    tertiary = Gold,
    background = BgLight,
    surface = Color.White,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
)

private val DarkColors = darkColorScheme(
    primary = PrimaryDark,
    onPrimary = OnPrimary,
    secondary = Secondary,
    tertiary = Gold,
    background = BgDark,
    surface = Color(0xFF1A1F24),
)

@Composable
fun AppTheme(darkTheme: Boolean = false, content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = Typography(),
        content = content,
    )
}
