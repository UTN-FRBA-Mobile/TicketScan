package com.example.ticketscan.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import java.math.BigDecimal
import java.math.RoundingMode

@Composable
fun AverageAmount(amount: BigDecimal) {
    val formatted = amount.setScale(2, RoundingMode.HALF_UP).toPlainString()
    Text(
        text = "Promedio por compra: $$formatted",
        style = MaterialTheme.typography.bodyLarge
    )
}