package com.example.ticketscan.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun TotalAmount(amount: Double) {
    Text(
        text = "Total: $${"%.2f".format(amount)}",
        style = MaterialTheme.typography.titleMedium
    )
}