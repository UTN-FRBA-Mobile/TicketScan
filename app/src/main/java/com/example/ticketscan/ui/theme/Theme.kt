package com.example.ticketscan.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable

@Composable
fun TicketScanTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    // Delegate to our centralized provider which already wires TicketScanTheme + MaterialTheme
    TicketScanThemeProvider(darkTheme = darkTheme, content = content)
}