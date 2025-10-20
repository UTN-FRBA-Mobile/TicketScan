package com.example.ticketscan.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Centralized spacing tokens for uniform paddings, margins and gap sizes.
 */
data class TicketScanSpacing(
    val none: Dp = 0.dp,
    val xs: Dp = 4.dp,
    val sm: Dp = 8.dp,
    val md: Dp = 12.dp,
    val lg: Dp = 16.dp,
    val xl: Dp = 24.dp,
    val xxl: Dp = 32.dp,
    val giant: Dp = 48.dp
)

internal val LocalTicketScanSpacing = staticCompositionLocalOf { TicketScanSpacing() }

object TicketScanSpaces {
    val current: TicketScanSpacing
        @Composable
        @ReadOnlyComposable
        get() = LocalTicketScanSpacing.current
}
