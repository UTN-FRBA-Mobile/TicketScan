package com.example.ticketscan.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.ticketscan.ui.theme.TicketScanIcons
import com.example.ticketscan.ui.theme.TicketScanTheme
import java.text.SimpleDateFormat
import java.util.Locale

data class FilterChipData(
    val label: String,
    val onClick: () -> Unit,
    val isActive: Boolean
)

@Composable
fun FilterChips(
    activeFilterCount: Int,
    storeFilter: String?,
    dateRange: Pair<Long?, Long?>?,
    amountRange: Pair<Double?, Double?>?,
    categoryFilter: String?,
    onStoreClick: () -> Unit,
    onDateRangeClick: () -> Unit,
    onAmountRangeClick: () -> Unit,
    onCategoryClick: () -> Unit,
    onClearAll: () -> Unit,
    modifier: Modifier = Modifier
) {
    val filters = mutableListOf<FilterChipData>()

    if (storeFilter != null) {
        filters.add(
            FilterChipData(
                label = "Tienda: $storeFilter",
                onClick = onStoreClick,
                isActive = true
            )
        )
    }

    if (dateRange != null && (dateRange.first != null || dateRange.second != null)) {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val startStr = dateRange.first?.let { dateFormat.format(it) } ?: "..."
        val endStr = dateRange.second?.let { dateFormat.format(it) } ?: "..."
        filters.add(
            FilterChipData(
                label = "Fecha: $startStr - $endStr",
                onClick = onDateRangeClick,
                isActive = true
            )
        )
    }

    if (amountRange != null && (amountRange.first != null || amountRange.second != null)) {
        val minStr = amountRange.first?.let { "$${String.format(Locale.US, "%.2f", it)}" } ?: "..."
        val maxStr = amountRange.second?.let { "$${String.format(Locale.US, "%.2f", it)}" } ?: "..."
        filters.add(
            FilterChipData(
                label = "Monto: $minStr - $maxStr",
                onClick = onAmountRangeClick,
                isActive = true
            )
        )
    }

    if (categoryFilter != null) {
        filters.add(
            FilterChipData(
                label = "Categoría: $categoryFilter",
                onClick = onCategoryClick,
                isActive = true
            )
        )
    }

    if (filters.isEmpty() && activeFilterCount > 0) {
        // Show generic filter chips if we have active filters but no specific data
        filters.add(
            FilterChipData(
                label = "Filtros activos ($activeFilterCount)",
                onClick = onClearAll,
                isActive = true
            )
        )
    }

    if (filters.isNotEmpty()) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(filters) { filter ->
                    AssistChip(
                        onClick = filter.onClick,
                        label = { Text(filter.label) },
                        colors = if (filter.isActive) {
                            AssistChipDefaults.assistChipColors(
                                containerColor = TicketScanTheme.colors.primaryContainer,
                                labelColor = TicketScanTheme.colors.onSurface
                            )
                        } else {
                            AssistChipDefaults.assistChipColors()
                        },
                        trailingIcon = {
                            Icon(
                                imageVector = TicketScanIcons.Close,
                                contentDescription = "Eliminar filtro",
                                modifier = Modifier.padding(start = 4.dp)
                            )
                        }
                    )
                }
            }

            if (activeFilterCount > 0) {
                AssistChip(
                    onClick = onClearAll,
                    label = { Text("Limpiar todo") },
                    colors = AssistChipDefaults.assistChipColors()
                )
            }
        }
    }
}

