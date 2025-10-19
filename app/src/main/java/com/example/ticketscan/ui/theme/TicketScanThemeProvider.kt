package com.example.ticketscan.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp

// Use centralized color tokens from Color.kt
private val LightColors = TicketScanColors(
    primary = Primary,
    secondary = Secondary,
    surface = Surface,
    onPrimary = Color.White,
    onSurface = OnSurface,
    background = Background,
    onBackground = OnBackground,
    surfaceVariant = SurfaceVariant,
    onSurfaceVariant = OnSurfaceVariant,
    outline = Outline,
    primaryContainer = PrimaryContainer,
    errorContainer = ErrorContainer,
    onErrorContainer = OnErrorContainer,
    error = Error,
    success = Success
)

private val DarkColors = TicketScanColors(
    primary = Primary,
    secondary = Secondary,
    surface = Color(0xFF0B2F2A),
    onPrimary = Color.Black,
    onSurface = Color(0xFFE6F7F2),
    background = Color(0xFF021512),
    onBackground = Color(0xFFE6F7F2),
    surfaceVariant = Color(0xFF072A26),
    onSurfaceVariant = Color(0xFF8FBFB5),
    outline = Color(0xFF37524A),
    primaryContainer = PrimaryContainer,
    errorContainer = ErrorContainer,
    onErrorContainer = OnErrorContainer,
    error = Error,
    success = Success
)

private val DefaultTypography = Typography()
// More prominent rounded shapes for a modern, friendly look
private val DefaultShapes = Shapes(
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(20.dp)
)

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
