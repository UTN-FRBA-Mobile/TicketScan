package com.example.ticketscan.ui.screens

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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.ticketscan.domain.model.TicketItem
import com.example.ticketscan.domain.repositories.TicketRepositoryMock
import com.example.ticketscan.ui.theme.TicketScanIcons
import com.example.ticketscan.ui.theme.TicketScanTheme
import com.example.ticketscan.ui.theme.TicketScanThemeProvider
import java.util.UUID


@Suppress("UNUSED_PARAMETER")
@Composable
fun TicketScreen(
    navController: NavController,
    viewModel: TicketViewModel
) {
    val ticket by viewModel.ticket.collectAsState()
    var isEditing by remember { mutableStateOf(false) }

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
                onItemChange = { id, name, price, quantity, categoryValue ->
                    viewModel.updateTicketItem(
                        itemId = id,
                        name = name,
                        price = price,
                        quantity = quantity,
                        category = categoryValue
                    )
                }
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
    }
}

@Composable
fun EditItemDialog(
    initial: TicketItem,
    onDismiss: () -> Unit,
    onSave: (name: String, price: Double?, quantity: Int?, category: String) -> Unit
) {
    var name by remember { mutableStateOf(initial.name) }
    var qtyText by remember { mutableStateOf(initial.quantity.toString()) }
    var priceText by remember { mutableStateOf(initial.price.toString()) }
    var category by remember { mutableStateOf(initial.category) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                val parsedQty = qtyText.toIntOrNull()
                val parsedPrice = priceText.toDoubleOrNull()
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
                TextField(value = name, onValueChange = { name = it }, label = { Text("Nombre") })
                Spacer(Modifier.height(8.dp))
                TextField(value = qtyText, onValueChange = { qtyText = it }, label = { Text("Cantidad") })
                Spacer(Modifier.height(8.dp))
                TextField(value = priceText, onValueChange = { priceText = it }, label = { Text("Precio") })
                Spacer(Modifier.height(8.dp))
                TextField(value = category, onValueChange = { category = it }, label = { Text("Categoría") })
            }
        }
    )
}

@Composable
fun CategorySection(
    category: String,
    items: List<TicketItem>,
    isEditable: Boolean = false,
    onItemChange: (id: UUID, name: String?, price: Double?, quantity: Int?, category: String?) -> Unit = { _, _, _, _, _ -> }
) {
    Column {
        Text(
            text = category,
            style = TicketScanTheme.typography.titleMedium,
            color = TicketScanTheme.colors.onBackground,
            modifier = Modifier
                .padding(vertical = 8.dp)
                .fillMaxWidth()

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
                onDismiss = { editingItem = null },
                onSave = { name, price, quantity, categoryValue ->
                    onItemChange(editingItem!!.id, name, price, quantity, categoryValue)
                    editingItem = null
                }
            )
        }

        Spacer(Modifier.height(8.dp))
        Text(
            text = "Total: $${items.sumOf { it.price * it.quantity }}",
            style = TicketScanTheme.typography.bodyLarge,
            color = TicketScanTheme.colors.primary,
            modifier = Modifier
                .padding(top = 4.dp)
                .fillMaxWidth(),
            textAlign = TextAlign.End
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TicketScreenPreview() {
    TicketScanThemeProvider {
        val navController = rememberNavController()
        val factory = remember { TicketViewModelFactory(TicketRepositoryMock(), UUID.randomUUID()) }
        val viewModel: TicketViewModel = viewModel(factory = factory)
        TicketScreen(navController = navController, viewModel)
    }
}
