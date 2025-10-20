package com.example.ticketscan.ui.theme

import androidx.compose.runtime.Composable
import com.example.ticketscan.domain.model.AppearancePreferences

@Composable
fun TicketScanTheme(
    appearance: AppearancePreferences = AppearancePreferences(),
    content: @Composable () -> Unit
) {
    // Delegate to our centralized provider which already wires TicketScanTheme + MaterialTheme
    TicketScanThemeProvider(appearance = appearance, content = content)
}