package com.example.ticketscan.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = TicketScanColors(
    primary = Color(0xFF006B5B),
    secondary = Color(0xFF4CAF50),
    surface = Color(0xFFFFFFFF),
    onPrimary = Color.White,
    onSurface = Color(0xFF1B1B1B),
    background = Color(0xFFF6F6F6),
    onBackground = Color(0xFF1B1B1B),
    surfaceVariant = Color(0xFFF2F3F2),
    onSurfaceVariant = Color(0xFF6F6F6F),
    outline = Color(0xFFBDBDBD),
    primaryContainer = Color(0xFFB2DFDB),
    errorContainer = Color(0xFFFDECEA),
    onErrorContainer = Color(0xFF610E0E)
)

private val DarkColors = TicketScanColors(
    primary = Color(0xFF4DD0E1),
    secondary = Color(0xFF81C784),
    surface = Color(0xFF121212),
    onPrimary = Color.Black,
    onSurface = Color(0xFFEDEDED),
    background = Color(0xFF000000),
    onBackground = Color(0xFFEDEDED),
    surfaceVariant = Color(0xFF1E1E1E),
    onSurfaceVariant = Color(0xFFBDBDBD),
    outline = Color(0xFF444444),
    primaryContainer = Color(0xFF004D40),
    errorContainer = Color(0xFF2A0A0A),
    onErrorContainer = Color(0xFFFFDAD6)
)

private val DefaultTypography = Typography()
private val DefaultShapes = Shapes()

@Composable
fun TicketScanThemeProvider(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkColors else LightColors

    TicketScanTheme(
        colors = colors,
        typography = DefaultTypography,
        shapes = DefaultShapes
    ) {
        // Also provide MaterialTheme for M3 components
        MaterialTheme(
            colorScheme = if (darkTheme) darkColorScheme() else lightColorScheme(),
            typography = DefaultTypography,
            shapes = DefaultShapes,
            content = content
        )
    }
}
