package com.example.ticketscan.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun PeriodSelector(selectedPeriod: Period, onPeriodChange: (Period) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        Period.entries.forEachIndexed { index, period ->
            FilterChip(
                selected = period == selectedPeriod,
                onClick = { onPeriodChange(period) },
                label = { Text(period.name) },
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }
    }
}

enum class Period {
    SEMANAL, MENSUAL
}