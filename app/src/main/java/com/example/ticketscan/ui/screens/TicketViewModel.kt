package com.example.ticketscan.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.ticketscan.domain.model.Ticket
import com.example.ticketscan.domain.repositories.TicketRepository
import com.example.ticketscan.domain.repositories.TicketRepositoryMock
import java.util.UUID
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class TicketViewModel(
    private val repository: TicketRepository = TicketRepositoryMock(),
    private val ticketId: UUID
) : ViewModel() {

    private val _ticket = MutableStateFlow<Ticket?>(null)
    val ticket: StateFlow<Ticket?> = _ticket

    init {
        loadTicket(ticketId)
    }

    private fun loadTicket(id: UUID) {
        viewModelScope.launch {
            repository.getTicket(id).collectLatest { result ->
                _ticket.value = result
            }
        }
    }

    fun updateTicketItem(itemId: UUID, name: String? = null, price: Double? = null, quantity: Int? = null, category: String? = null) {
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
        _ticket.value = currentTicket.copy(items = updatedItems, total = updatedItems.sumOf { it.price * it.quantity })
    }
}

class TicketViewModelFactory(
    private val repository: TicketRepository,
    private val ticketId: UUID
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TicketViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TicketViewModel(repository, ticketId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}