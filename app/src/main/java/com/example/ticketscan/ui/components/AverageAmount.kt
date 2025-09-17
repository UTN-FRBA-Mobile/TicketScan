package com.example.ticketscan.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun AverageAmount(amount: Double) {
    Text(
        text = "Promedio por compra: $${"%.2f".format(amount)}",
        style = MaterialTheme.typography.bodyLarge
    )
}