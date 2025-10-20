package com.example.ticketscan.ui.screens

import android.app.DatePickerDialog
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.ticketscan.domain.model.Category
import com.example.ticketscan.domain.model.TicketItem
import com.example.ticketscan.domain.viewmodel.RepositoryViewModel
import com.example.ticketscan.domain.viewmodel.RepositoryViewModelFactory
import com.example.ticketscan.ui.components.CategorySection
import com.example.ticketscan.ui.components.EditItemDialog
import com.example.ticketscan.ui.components.EditStoreDialog
import com.example.ticketscan.ui.components.TicketActionButtons
import com.example.ticketscan.ui.components.TicketHeader
import com.example.ticketscan.ui.theme.TicketScanTheme
import com.example.ticketscan.ui.theme.TicketScanThemeProvider
import java.util.Calendar
import java.util.UUID

@Suppress("UNUSED_PARAMETER")
@Composable
fun TicketScreen(
    navController: NavController,
    viewModel: TicketViewModel
) {
    val context = LocalContext.current
    val ticket by viewModel.ticket.collectAsState()
    val categories by viewModel.categories.collectAsState()
    var isEditing by remember { mutableStateOf(false) }
    var creatingItem by remember { mutableStateOf<TicketItem?>(null) }
    var editingStore by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        TicketHeader(
            title = if (isEditing) "Editar Ticket" else "Ticket",
            date = ticket?.date,
            store = ticket?.store,
            isEditing = isEditing,
            onEditDate = {
                ticket?.date?.let { currentDate ->
                    val calendar = Calendar.getInstance().apply { time = currentDate }
                    DatePickerDialog(
                        context,
                        { _, year, month, dayOfMonth ->
                            val newCalendar = Calendar.getInstance().apply {
                                time = currentDate
                                set(Calendar.YEAR, year)
                                set(Calendar.MONTH, month)
                                set(Calendar.DAY_OF_MONTH, dayOfMonth)
                            }
                            viewModel.updateTicketDate(newCalendar.time)
                        },
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)
                    ).show()
                }
            },
            onEditStore = { editingStore = true }
        )

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

        val ticketTotal = ticket?.items?.sumOf { it.price * it.quantity } ?: 0.0
        Text(
            text = "Total ticket: $${"%.2f".format(ticketTotal)}",
            style = TicketScanTheme.typography.headlineSmall,
            color = TicketScanTheme.colors.primary,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            textAlign = TextAlign.End
        )

        Spacer(Modifier.weight(1f))

        TicketActionButtons(
            isEditing = isEditing,
            onCancelEdit = {
                viewModel.refreshTicket()
                isEditing = false
            },
            onAddItem = {
                val defaultCategory = categories.firstOrNull() ?: Category.default()
                creatingItem = TicketItem(
                    id = UUID.randomUUID(),
                    name = "",
                    category = defaultCategory,
                    quantity = 0,
                    isIntUnit = true,
                    price = 0.0
                )
            },
            onSave = {
                viewModel.saveTicket()
                isEditing = false
            },
            onDelete = {
                viewModel.deleteTicket {
                    navController.navigate("home") {
                        popUpTo("home") { inclusive = false }
                    }
                }
            },
            onEdit = { isEditing = true }
        )

        // Diálogo para crear/editar artículo
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

        // Diálogo para editar/crear tienda
        if (editingStore) {
            EditStoreDialog(
                initial = ticket?.store,
                onDismiss = { editingStore = false },
                onSave = { name, cuit, location ->
                    viewModel.updateTicketStore(name, cuit, location)
                    editingStore = false
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TicketScreenPreview() {
    TicketScanThemeProvider {
        val navController = rememberNavController()
        val repositoryViewModelFactory = RepositoryViewModelFactory(context = LocalContext.current)
        val repositoryViewModel: RepositoryViewModel = viewModel(factory = repositoryViewModelFactory)
        val factory = remember { TicketViewModelFactory(repositoryViewModel, UUID.randomUUID()) }
        val viewModel: TicketViewModel = viewModel(factory = factory)
        TicketScreen(navController = navController, viewModel)
    }
}

