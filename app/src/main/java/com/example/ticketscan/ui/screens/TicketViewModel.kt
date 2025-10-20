package com.example.ticketscan.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.ticketscan.domain.model.Category
import com.example.ticketscan.domain.model.Store
import com.example.ticketscan.domain.model.Ticket
import com.example.ticketscan.domain.viewmodel.RepositoryViewModel
import java.util.Date
import java.util.UUID
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TicketViewModel (
    private val repositoryViewModel: RepositoryViewModel,
    private val ticketId: UUID
) : ViewModel() {

    private val _ticket = MutableStateFlow<Ticket?>(null)
    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val ticket: StateFlow<Ticket?> = _ticket
    val categories: StateFlow<List<Category>> = _categories

    init {
        loadTicket(ticketId)
        loadCategories()
    }

    private fun loadTicket(id: UUID) {
        viewModelScope.launch {
            _ticket.value = repositoryViewModel.getTicketById(id)
        }
    }

    private fun loadCategories() {
        viewModelScope.launch {
            _categories.value = repositoryViewModel.getAllCategories()
        }
    }

    fun refreshTicket() {
        loadTicket(ticketId)
    }

    fun updateTicketItem(itemId: UUID, name: String? = null, price: Double? = null, quantity: Int? = null, category: Category? = null) {
        val currentTicket = _ticket.value ?: return
        val updatedItems = currentTicket.items.map { item ->
            if (item.id == itemId) {
                item.copy(
                    name = name ?: item.name,
                    price = price ?: item.price,
                    quantity = quantity ?: item.quantity,
                    category = category ?: item.category
                )
            } else item
        }
        _ticket.value = currentTicket.copy(items = updatedItems, total = updatedItems.sumOf {
            it.price
        })
    }

    fun addTicketItem(item: com.example.ticketscan.domain.model.TicketItem) {
        val currentTicket = _ticket.value ?: return
        val updatedItems = currentTicket.items + item
        _ticket.value = currentTicket.copy(items = updatedItems, total = updatedItems.sumOf {
            it.price
        })
    }

    fun removeTicketItem(itemId: UUID) {
        val currentTicket = _ticket.value ?: return
        val updatedItems = currentTicket.items.filter { it.id != itemId }
        _ticket.value = currentTicket.copy(items = updatedItems, total = updatedItems.sumOf {
            it.price
        })
    }

    fun updateTicketDate(newDate: Date) {
        val currentTicket = _ticket.value ?: return
        _ticket.value = currentTicket.copy(date = newDate)
    }

    fun updateTicketStore(name: String, cuit: Long, location: String) {
        viewModelScope.launch {
            val currentTicket = _ticket.value ?: return@launch

            // Buscar tienda existente por nombre o CUIT
            val allStores = repositoryViewModel.getAllStores()
            var store = allStores.find { it.name == name || it.cuit == cuit }

            if (store == null) {
                // Si no existe, crear nueva tienda
                val newStore = Store(
                    id = UUID.randomUUID(),
                    name = name,
                    cuit = cuit,
                    location = location
                )
                repositoryViewModel.insertStore(newStore) { success ->
                    if (success) {
                        store = newStore
                    }
                }
                store = newStore
            }

            // Actualizar el ticket con la tienda
            _ticket.value = currentTicket.copy(store = store)
        }
    }

    fun saveTicket() {
        val currentTicket = _ticket.value ?: return
        viewModelScope.launch {
            repositoryViewModel.updateTicket(currentTicket) {
                loadTicket(ticketId)
            }
        }
    }

    fun deleteTicket(onDeleted: () -> Unit) {
        val currentTicket = _ticket.value ?: return
        viewModelScope.launch {
            repositoryViewModel.deleteTicket(currentTicket) {
                onDeleted()
            }
        }
    }
}

class TicketViewModelFactory(
    private val repositoryViewModel: RepositoryViewModel,
    private val ticketId: UUID
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TicketViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TicketViewModel(repositoryViewModel, ticketId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}