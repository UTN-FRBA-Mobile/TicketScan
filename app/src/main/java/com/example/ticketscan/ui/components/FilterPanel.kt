package com.example.ticketscan.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.example.ticketscan.domain.model.Category
import com.example.ticketscan.domain.model.Store
import com.example.ticketscan.ui.screens.AmountRange
import com.example.ticketscan.ui.screens.DateRange
import com.example.ticketscan.ui.theme.TicketScanIcons
import com.example.ticketscan.ui.theme.TicketScanTheme
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterPanel(
    ticketTitleSearch: String,
    onTicketTitleSearchChange: (String) -> Unit,
    storeFilter: String?,
    onStoreFilterChange: (String?) -> Unit,
    availableStores: List<Store>,
    categoryFilter: String?,
    onCategoryFilterChange: (String?) -> Unit,
    availableCategories: List<Category>,
    dateRange: DateRange?,
    onDateRangeChange: (DateRange?) -> Unit,
    amountRange: AmountRange?,
    onAmountRangeChange: (AmountRange?) -> Unit,
    activeFilterCount: Int,
    onClearAll: () -> Unit,
    amountError: String?,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(activeFilterCount > 0) }
    var ticketTitleTextFieldValue by remember(ticketTitleSearch) {
        mutableStateOf(TextFieldValue(ticketTitleSearch))
    }
    var storeTextFieldValue by remember(storeFilter) {
        mutableStateOf(TextFieldValue(storeFilter ?: ""))
    }
    var debouncedTicketTitle by remember { mutableStateOf(ticketTitleSearch) }
    var debouncedStore by remember { mutableStateOf(storeFilter) }

    // Debounce ticket title search
    LaunchedEffect(ticketTitleTextFieldValue.text) {
        delay(300)
        debouncedTicketTitle = ticketTitleTextFieldValue.text
        onTicketTitleSearchChange(debouncedTicketTitle)
    }

    // Debounce store filter
    LaunchedEffect(storeTextFieldValue.text) {
        delay(300)
        debouncedStore = storeTextFieldValue.text.takeIf { it.isNotBlank() }
        onStoreFilterChange(debouncedStore)
    }

    TicketScanCard(
        style = TicketScanCardStyle.Tonal,
        modifier = modifier.fillMaxWidth()
    ) {
        // Header with expand/collapse button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (isExpanded) "Filtros" else "Filtros (${activeFilterCount} activos)",
                style = TicketScanTheme.typography.titleLarge,
                color = TicketScanTheme.colors.onSurface
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (activeFilterCount > 0) {
                    TextButton(onClick = onClearAll) {
                        Text("Limpiar todo")
                    }
                }
                IconButton(onClick = { isExpanded = !isExpanded }) {
                    Icon(
                        imageVector = if (isExpanded) TicketScanIcons.ArrowUpward else TicketScanIcons.ArrowDownward,
                        contentDescription = if (isExpanded) "Ocultar filtros" else "Mostrar filtros"
                    )
                }
            }
        }

        if (isExpanded) {
            Spacer(modifier = Modifier.height(TicketScanTheme.spacing.md))

            // Ticket Title Search
            OutlinedTextField(
                value = ticketTitleTextFieldValue.text,
                onValueChange = { ticketTitleTextFieldValue = TextFieldValue(it) },
                label = { Text("Buscar en tickets") },
                placeholder = { Text("Buscar en productos y tiendas...") },
                leadingIcon = {
                    Icon(
                        imageVector = TicketScanIcons.Search,
                        contentDescription = "Buscar"
                    )
                },
                trailingIcon = {
                    if (ticketTitleTextFieldValue.text.isNotEmpty()) {
                        IconButton(onClick = {
                            ticketTitleTextFieldValue = TextFieldValue("")
                            onTicketTitleSearchChange("")
                        }) {
                            Icon(
                                imageVector = TicketScanIcons.Close,
                                contentDescription = "Limpiar búsqueda"
                            )
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = TicketScanTheme.colors.surface,
                    unfocusedContainerColor = TicketScanTheme.colors.surface
                )
            )

            Spacer(modifier = Modifier.height(TicketScanTheme.spacing.md))

            // Store Filter
            OutlinedTextField(
                value = storeTextFieldValue.text,
                onValueChange = { storeTextFieldValue = TextFieldValue(it) },
                label = { Text("Tienda") },
                placeholder = { Text("Filtrar por tienda...") },
                trailingIcon = {
                    if (storeTextFieldValue.text.isNotEmpty()) {
                        IconButton(onClick = {
                            storeTextFieldValue = TextFieldValue("")
                            onStoreFilterChange(null)
                        }) {
                            Icon(
                                imageVector = TicketScanIcons.Close,
                                contentDescription = "Limpiar tienda"
                            )
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = TicketScanTheme.colors.surface,
                    unfocusedContainerColor = TicketScanTheme.colors.surface
                )
            )

            Spacer(modifier = Modifier.height(TicketScanTheme.spacing.md))

            // Category Dropdown
            CategoryFilterDropdown(
                selectedCategory = categoryFilter,
                categories = availableCategories,
                onCategorySelected = onCategoryFilterChange,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(TicketScanTheme.spacing.md))

            // Amount Range
            AmountRangeFilterInputs(
                minAmount = amountRange?.min,
                maxAmount = amountRange?.max,
                onMinAmountChange = { min ->
                    onAmountRangeChange(
                        AmountRange(
                            min = min,
                            max = amountRange?.max
                        )
                    )
                },
                onMaxAmountChange = { max ->
                    onAmountRangeChange(
                        AmountRange(
                            min = amountRange?.min,
                            max = max
                        )
                    )
                },
                modifier = Modifier.fillMaxWidth()
            )

            if (amountError != null) {
                Text(
                    text = amountError,
                    fontSize = TicketScanTheme.typography.bodySmall.fontSize,
                    color = TicketScanTheme.colors.error,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

