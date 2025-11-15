package com.example.ticketscan.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.ticketscan.domain.model.Category
import com.example.ticketscan.ui.theme.TicketScanIcons
import com.example.ticketscan.ui.theme.TicketScanTheme

@androidx.compose.material3.ExperimentalMaterial3Api
@Composable
fun CategoryFilterDropdown(
    selectedCategory: String?,
    categories: List<Category>,
    onCategorySelected: (String?) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val displayValue = selectedCategory ?: "Todas las categorías"

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = displayValue,
            onValueChange = { },
            readOnly = true,
            label = { Text("Categoría") },
            trailingIcon = {
                Row {
                    if (selectedCategory != null) {
                        IconButton(onClick = { onCategorySelected(null) }) {
                            Icon(
                                imageVector = TicketScanIcons.Close,
                                contentDescription = "Limpiar categoría"
                            )
                        }
                    }
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                }
            },
            colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                focusedContainerColor = TicketScanTheme.colors.surface,
                unfocusedContainerColor = TicketScanTheme.colors.surface
            ),
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text("Todas las categorías") },
                onClick = {
                    onCategorySelected(null)
                    expanded = false
                }
            )
            categories.forEach { category ->
                DropdownMenuItem(
                    text = { Text(category.name) },
                    onClick = {
                        onCategorySelected(category.name)
                        expanded = false
                    }
                )
            }
        }
    }
}

