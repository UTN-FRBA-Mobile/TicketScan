package com.example.ticketscan.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.ticketscan.domain.model.Category
import com.example.ticketscan.domain.model.TicketItem
import com.example.ticketscan.domain.repositories.TicketRepositoryMock
import com.example.ticketscan.ui.theme.TicketScanIcons
import com.example.ticketscan.ui.theme.TicketScanTheme
import com.example.ticketscan.ui.theme.TicketScanThemeProvider
import java.util.UUID


@RequiresApi(Build.VERSION_CODES.O)
@Suppress("UNUSED_PARAMETER")
@Composable
fun TicketScreen(
    navController: NavController,
    viewModel: TicketViewModel
) {
    val ticket by viewModel.ticket.collectAsState()
    val categories by viewModel.categories.collectAsState()
    var isEditing by remember { mutableStateOf(false) }
    var creatingItem by remember { mutableStateOf<TicketItem?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        Spacer(Modifier.height(8.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(
                text = if (isEditing) "Editar Ticket ###" else "Ticket ###",
                style = TicketScanTheme.typography.headlineLarge,
                color = TicketScanTheme.colors.onBackground
            )
            Text(
                text = ticket?.date?.toString() ?: "Fecha xx/xx/xxxx",
                style = TicketScanTheme.typography.bodyLarge,
                color = TicketScanTheme.colors.onBackground
            )
        }

        Spacer(Modifier.height(20.dp))

        val itemsByCategory = ticket?.items?.groupBy { it.category } ?: emptyMap()
        itemsByCategory.forEach { (category, items) ->
            CategorySection(
                category = category,
                items = items,
                isEditable = isEditing,
                categories = categories,
                onItemChange = { id, name, price, quantity, categoryValue ->
                    viewModel.updateTicketItem(
                        itemId = id,
                        name = name,
                        price = price,
                        quantity = quantity,
                        category = categoryValue
                    )
                },
                onItemDelete = { id -> viewModel.removeTicketItem(id) }
            )
            Spacer(Modifier.height(12.dp))
        }

        Spacer(Modifier.weight(1f))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            if (isEditing) {
                IconButton(onClick = {
                    viewModel.refreshTicket()
                    isEditing = false
                }) {
                    Icon(
                        imageVector = TicketScanIcons.Close,
                        contentDescription = "Cancelar edición",
                        tint = TicketScanTheme.colors.onBackground,
                        modifier = Modifier.size(40.dp)
                    )
                }

                IconButton(onClick = {
                    val defaultCategory = categories.firstOrNull() ?: Category.Other
                    creatingItem = TicketItem(
                        id = UUID.randomUUID(),
                        name = "",
                        category = defaultCategory,
                        quantity = 0,
                        isIntUnit = true,
                        price = 0.0
                    )
                }) {
                    Icon(
                        imageVector = TicketScanIcons.Add,
                        contentDescription = "Crear artículo",
                        tint = TicketScanTheme.colors.onBackground,
                        modifier = Modifier.size(40.dp)
                    )
                }

                IconButton(onClick = {
                    viewModel.saveTicket()
                    isEditing = false
                }) {
                    Icon(
                        imageVector = TicketScanIcons.Save,
                        contentDescription = "Guardar",
                        tint = TicketScanTheme.colors.onBackground,
                        modifier = Modifier.size(40.dp)
                    )
                }
            } else {
                IconButton(onClick = { /* TODO eliminar ticket */ }) {
                    Icon(
                        imageVector = TicketScanIcons.Close,
                        contentDescription = "Eliminar ticket",
                        tint = TicketScanTheme.colors.onBackground,
                        modifier = Modifier.size(40.dp)
                    )
                }

                IconButton(onClick = {
                    isEditing = true
                }) {
                    Icon(
                        imageVector = TicketScanIcons.Edit,
                        contentDescription = "Editar",
                        tint = TicketScanTheme.colors.onBackground,
                        modifier = Modifier.size(40.dp)
                    )
                }
            }
        }

        if (creatingItem != null) {
            EditItemDialog(
                initial = creatingItem!!,
                categories = categories,
                onDismiss = { creatingItem = null },
                onSave = { name, price, quantity, categoryValue ->
                    val item = TicketItem(
                        id = creatingItem!!.id,
                        name = name,
                        category = categoryValue,
                        quantity = quantity ?: 0,
                        isIntUnit = true,
                        price = price ?: 0.0
                    )
                    viewModel.addTicketItem(item)
                    creatingItem = null
                },
                onDelete = null
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditItemDialog(
    initial: TicketItem,
    categories: List<Category>,
    onDismiss: () -> Unit,
    onSave: (name: String, price: Double?, quantity: Int?, category: Category) -> Unit,
    onDelete: ((UUID) -> Unit)? = null
) {
    var name by remember { mutableStateOf(initial.name) }
    var qtyText by remember { mutableStateOf(initial.quantity.toString()) }
    var priceText by remember { mutableStateOf(initial.price.toString()) }
    var category by remember { mutableStateOf(initial.category) }
    var expanded by remember { mutableStateOf(false) }
    var showConfirmDelete by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                val parsedQty = qtyText.toIntOrNull()?.coerceAtLeast(0) ?: 0
                val parsedPrice = priceText.toDoubleOrNull()?.coerceAtLeast(0.0) ?: 0.0
                onSave(name, parsedPrice, parsedQty, category)
            }) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        },
        title = { Text("Editar artículo") },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre") }
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = qtyText,
                    onValueChange = { if (it.all { c -> c.isDigit() }) qtyText = it },
                    label = { Text("Cantidad") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    )
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = priceText,
                    onValueChange = {
                        if (it.isEmpty() || it.matches(Regex("^\\d*\\.?\\d*"))) priceText = it
                    },
                    label = { Text("Precio") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal,
                        imeAction = ImeAction.Next
                    )
                )
                Spacer(Modifier.height(8.dp))
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = category.name,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Categoría") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor()
                    )
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.exposedDropdownSize(true)
                    ) {
                        categories.forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(cat.name) },
                                onClick = {
                                    category = cat
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                if (onDelete != null) {
                    Spacer(Modifier.height(12.dp))
                    TextButton(
                        onClick = { showConfirmDelete = true },
                    ) {
                        Text("Eliminar", color = Color.Red)
                    }

                    if (showConfirmDelete) {
                        AlertDialog(
                            onDismissRequest = { showConfirmDelete = false },
                            title = { Text("Confirmar eliminación") },
                            text = { Text("¿Estás seguro de que quieres eliminar este artículo?") },
                            confirmButton = {
                                TextButton(onClick = {
                                    onDelete(initial.id)
                                    showConfirmDelete = false
                                    onDismiss()
                                }) { Text("Eliminar", color = Color.Red) }
                            },
                            dismissButton = {
                                TextButton(onClick = { showConfirmDelete = false }) { Text("Cancelar") }
                            }
                        )
                    }
                }
            }
        }
    )
}

@Composable
fun CategorySection(
    category: Category,
    items: List<TicketItem>,
    categories: List<Category>,
    isEditable: Boolean = false,
    onItemChange: (id: UUID, name: String?, price: Double?, quantity: Int?, category: Category?) -> Unit = { _, _, _, _, _ -> },
    onItemDelete: (id: UUID) -> Unit = {}
) {
    Column {
        Text(
            text = category.name,
            style = TicketScanTheme.typography.titleMedium,
            color = TicketScanTheme.colors.onBackground,
            modifier = Modifier
                .padding(vertical = 8.dp)
                .fillMaxWidth(),
        )
        var editingItem by remember { mutableStateOf<TicketItem?>(null) }

        items.forEach { item ->
            Row(
                modifier = Modifier.fillMaxWidth()
                    .padding(vertical = 10.dp)
                    .border(BorderStroke(1.dp, TicketScanTheme.colors.primary), TicketScanTheme.shapes.small),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = "${item.name} (${item.quantity} u.) - $${item.price}",
                    style = TicketScanTheme.typography.bodyMedium,
                    color = TicketScanTheme.colors.onBackground,
                    modifier = Modifier.padding(PaddingValues(horizontal = 15.dp))
                )
                if (isEditable) {
                    Spacer(Modifier.width(8.dp))
                    IconButton(
                        onClick = { editingItem = item },
                        modifier = Modifier.padding(0.dp).size(24.dp),

                    ) {
                        Icon(
                            imageVector = TicketScanIcons.Edit,
                            contentDescription = "Editar",
                            tint = TicketScanTheme.colors.onBackground,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        if (editingItem != null) {
            EditItemDialog(
                initial = editingItem!!,
                categories = categories,
                onDismiss = { editingItem = null },
                onSave = { name, price, quantity, categoryValue ->
                    onItemChange(editingItem!!.id, name, price, quantity, categoryValue)
                    editingItem = null
                },
                onDelete = { id ->
                    onItemDelete(id)
                    editingItem = null
                }
            )
        }

        Spacer(Modifier.height(8.dp))
        Text(
            text = "Total: $${items.sumOf { it.price }}",
            style = TicketScanTheme.typography.bodyLarge,
            color = TicketScanTheme.colors.primary,
            modifier = Modifier
                .padding(top = 4.dp)
                .fillMaxWidth(),
            textAlign = TextAlign.End
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun TicketScreenPreview() {
    TicketScanThemeProvider {
        val navController = rememberNavController()
        val factory = remember { TicketViewModelFactory(TicketRepositoryMock, UUID.randomUUID()) }
        val viewModel: TicketViewModel = viewModel(factory = factory)
        TicketScreen(navController = navController, viewModel)
    }
}
