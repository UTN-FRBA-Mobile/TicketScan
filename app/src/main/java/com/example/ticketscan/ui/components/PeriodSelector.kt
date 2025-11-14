package com.example.ticketscan.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.ticketscan.ui.theme.TicketScanTheme

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
                modifier = Modifier.padding(horizontal = 8.dp),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = TicketScanTheme.colors.primary,
                    selectedLabelColor = TicketScanTheme.colors.onPrimary,
                    containerColor = TicketScanTheme.colors.surfaceVariant,
                    labelColor = TicketScanTheme.colors.onSurfaceVariant
                )
            )
        }
    }
}

enum class Period {
    SEMANAL, MENSUAL
}
