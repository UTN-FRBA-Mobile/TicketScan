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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
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

        Spacer(Modifier.weight(1f)) // push actions to bottom

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            if (!isEditing) {
                IconButton(onClick = { /* TODO eliminar ticket */ }) {
                    Icon(
                        imageVector = TicketScanIcons.Close,
                        contentDescription = "Eliminar",
                        tint = TicketScanTheme.colors.onBackground,
                        modifier = Modifier.size(40.dp)
                    )
                }
            }

            IconButton(onClick = {
                if (isEditing) {
                    // cancelar edición
                    viewModel.refreshTicket()
                    isEditing = false
                } else {
                    isEditing = true
                }
            }) {
                Icon(
                    imageVector = if (isEditing) TicketScanIcons.Close else TicketScanIcons.Edit,
                    contentDescription = if (isEditing) "Cerrar edición" else "Editar",
                    tint = TicketScanTheme.colors.onBackground,
                    modifier = Modifier.size(40.dp)
                )
            }

            // Save or Share
            IconButton(onClick = {
                if (isEditing) {
                    viewModel.saveTicket()
                    isEditing = false
                } else {
                    /* TODO mostrar opciones para exportar ticket */
                }
            }) {
                Icon(
                    imageVector = if (isEditing) TicketScanIcons.Save else TicketScanIcons.Share,
                    contentDescription = if (isEditing) "Guardar" else "Compartir",
                    tint = TicketScanTheme.colors.onBackground,
                    modifier = Modifier.size(40.dp)
                )
            }
        }
    }
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
        items.forEach { item ->
            Row(
                modifier = Modifier.fillMaxWidth()
                    .padding(vertical = 10.dp)
                    .border(BorderStroke(1.dp, TicketScanTheme.colors.primary), TicketScanTheme.shapes.small),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                if (isEditable) {
                    TextField(
                        value = item.name,
                        onValueChange = { newName -> onItemChange(item.id, newName, null, null, null) },
                        modifier = Modifier
                            .padding(PaddingValues(start = 12.dp))
                            .width(160.dp)
                    )

                    TextField(
                        value = item.quantity.toString(),
                        onValueChange = { newQtyStr ->
                            val parsed = newQtyStr.toIntOrNull()
                            onItemChange(item.id, null, null, parsed, null)
                        },
                        modifier = Modifier.width(80.dp)
                    )

                    TextField(
                        value = item.price.toString(),
                        onValueChange = { newPriceStr ->
                            val parsed = newPriceStr.toDoubleOrNull()
                            onItemChange(item.id, null, parsed, null, null)
                        },
                        modifier = Modifier.width(100.dp)
                    )

                    IconButton(
                        onClick = { /* TODO eliminar ticket */ },
                        modifier = Modifier.padding(0.dp).size(24.dp),

                    ) {
                        Icon(
                            imageVector = TicketScanIcons.Edit,
                            contentDescription = "Editar",
                            tint = TicketScanTheme.colors.onBackground,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                } else {
                    Text(
                        text = "${item.name} (${item.quantity} u.) - $${item.price}",
                        style = TicketScanTheme.typography.bodyMedium,
                        color = TicketScanTheme.colors.onBackground,
                        modifier = Modifier.padding(PaddingValues(horizontal = 15.dp))
                    )
                }
            }
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
