package com.example.ticketscan.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.material3.Shapes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

// Define your theme colors, typography, and shapes
// You can extend these as needed

data class TicketScanColors(
    val primary: Color,
    val secondary: Color,
    val surface: Color,
    val onPrimary: Color,
    val onSurface: Color,
    val background: Color,
    val onBackground: Color,
    // Additional tokens used across the app
    val surfaceVariant: Color,
    val onSurfaceVariant: Color,
    val outline: Color,
    val primaryContainer: Color,
    // Error tokens
    val errorContainer: Color,
    val onErrorContainer: Color
)

data class TicketScanThemeData(
    val colors: TicketScanColors,
    val typography: Typography,
    val shapes: Shapes
)

val LocalTicketScanTheme = staticCompositionLocalOf<TicketScanThemeData> {
    error("No TicketScanTheme provided")
}

@Composable
fun TicketScanTheme(
    colors: TicketScanColors,
    typography: Typography,
    shapes: Shapes,
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(
        LocalTicketScanTheme provides TicketScanThemeData(colors, typography, shapes),
        content = content
    )
}

object TicketScanTheme {
    val colors: TicketScanColors
        @Composable @ReadOnlyComposable get() = LocalTicketScanTheme.current.colors
    val typography: Typography
        @Composable @ReadOnlyComposable get() = LocalTicketScanTheme.current.typography
    val shapes: Shapes
        @Composable @ReadOnlyComposable get() = LocalTicketScanTheme.current.shapes
}
