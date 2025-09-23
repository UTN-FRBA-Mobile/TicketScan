package com.example.ticketscan.domain.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ticketscan.domain.model.*
import com.example.ticketscan.domain.repositories.category.CategoryRepositorySQLite
import com.example.ticketscan.domain.repositories.store.StoreRepositorySQLite
import com.example.ticketscan.domain.repositories.ticket.TicketRepositorySQLite
import com.example.ticketscan.domain.repositories.ticketitem.TicketItemRepositorySQLite
import com.example.ticketscan.domain.repositories.stats.StatsRepositorySQLite
import com.example.ticketscan.ui.components.Period
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

class RepositoryViewModel(
    private val storeRepo: StoreRepositorySQLite,
    private val categoryRepo: CategoryRepositorySQLite,
    private val ticketRepo: TicketRepositorySQLite,
    private val ticketItemRepo: TicketItemRepositorySQLite,
    private val statsRepo: StatsRepositorySQLite
) : ViewModel() {
    // STORE
    fun insertStore(store: Store, onResult: (Boolean) -> Unit) = viewModelScope.launch {
        val result = withContext(Dispatchers.IO) { storeRepo.insertStore(store) }
        onResult(result)
    }
    suspend fun getStoreById(id: UUID) = withContext(Dispatchers.IO) { storeRepo.getStoreById(id) }
    suspend fun getAllStores() = withContext(Dispatchers.IO) { storeRepo.getAllStores() }

    // CATEGORY
    fun insertCategory(category: Category, onResult: (Boolean) -> Unit) = viewModelScope.launch {
        val result = withContext(Dispatchers.IO) { categoryRepo.insertCategory(category) }
        onResult(result)
    }
    suspend fun getCategoryById(id: UUID) = withContext(Dispatchers.IO) { categoryRepo.getCategoryById(id) }
    suspend fun getAllCategories() = withContext(Dispatchers.IO) { categoryRepo.getAllCategories() }

    // TICKET
    fun insertTicket(ticket: Ticket, onResult: (Boolean) -> Unit) = viewModelScope.launch {
        val result = withContext(Dispatchers.IO) { ticketRepo.insertTicket(ticket) }
        onResult(result)
    }
    suspend fun getTicketById(id: UUID) = withContext(Dispatchers.IO) { ticketRepo.getTicketById(id) }
    suspend fun getAllTickets() = withContext(Dispatchers.IO) { ticketRepo.getAllTickets() }

    // TICKET ITEM
    fun insertTicketItem(item: TicketItem, onResult: (Boolean) -> Unit) = viewModelScope.launch {
        val result = withContext(Dispatchers.IO) { ticketItemRepo.insertItem(item) }
        onResult(result)
    }
    suspend fun getTicketsItemByTicketId(id: UUID) = withContext(Dispatchers.IO) { ticketItemRepo.getItemsByTicketId(id) }

    // STATS
    suspend fun getCategoryStats(period: Period) = withContext(Dispatchers.IO) { statsRepo.getCategoryStats(period) }
}