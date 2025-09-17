package com.example.ticketscan.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun PeriodSelector(selectedPeriod: Period, onPeriodChange: (Period) -> Unit) {
    Row {
        Period.entries.forEach { period ->
            FilterChip(
                selected = period == selectedPeriod,
                onClick = { onPeriodChange(period) },
                label = { Text(period.name) },
                modifier = Modifier.padding(end = 8.dp)
            )
        }
    }
}

enum class Period {
    SEMANAL, MENSUAL
}