package com.example.ticketscan.ui.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.ticketscan.ui.theme.TicketScanTheme
import java.math.BigDecimal
import java.math.RoundingMode

@Composable
fun ComparisonSection(current: BigDecimal, previous: BigDecimal) {
    val difference = current.subtract(previous)
    val color = if (difference >= BigDecimal.ZERO) Color.Red else Color.Green
    val sign = if (difference >= BigDecimal.ZERO) "+" else "-"

    val formattedDifference = difference
        .abs()
        .setScale(2, RoundingMode.HALF_UP)
        .toPlainString()

    Text(
        text = "Cambio respecto al período anterior: $sign$$formattedDifference",
        color = color,
        style = TicketScanTheme.typography.bodyMedium
    )
}