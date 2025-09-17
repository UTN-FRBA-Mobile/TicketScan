package com.example.ticketscan.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun ComparisonSection(current: Double, previous: Double) {
    val difference = current - previous
    val color = if (difference >= 0) Color.Red else Color.Green
    val sign = if (difference >= 0) "+" else "-"

    Text(
        text = "Cambio respecto al período anterior: $sign$${"%.2f".format(kotlin.math.abs(difference))}",
        color = color,
        style = MaterialTheme.typography.bodyMedium
    )
}