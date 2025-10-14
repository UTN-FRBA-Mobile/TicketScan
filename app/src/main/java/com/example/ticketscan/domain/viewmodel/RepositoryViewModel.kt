package com.example.ticketscan.domain.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ticketscan.domain.model.*
import com.example.ticketscan.domain.repositories.category.CategoryRepository
import com.example.ticketscan.domain.repositories.stats.StatsRepository
import com.example.ticketscan.domain.repositories.store.StoreRepository
import com.example.ticketscan.domain.repositories.ticket.TicketRepository
import com.example.ticketscan.domain.repositories.ticketitem.TicketItemRepository
import com.example.ticketscan.ui.components.Period
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

class RepositoryViewModel(
    private val storeRepo: StoreRepository,
    private val categoryRepo: CategoryRepository,
    private val ticketRepo: TicketRepository,
    private val ticketItemRepo: TicketItemRepository,
    private val statsRepo: StatsRepository
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
    fun deleteTicket(ticket: Ticket, onResult: (Boolean) -> Unit) = viewModelScope.launch {
        val result = withContext(Dispatchers.IO) { ticketRepo.deleteTicket(ticket.id) }
        onResult(result)
    }
    fun updateTicket(ticket: Ticket, onResult: (Boolean) -> Unit) = viewModelScope.launch {
        val result = withContext(Dispatchers.IO) {
            ticketRepo.deleteTicket(ticket.id)
            ticketRepo.insertTicket(ticket)
        }
        onResult(result)
    }
    suspend fun getTicketById(id: UUID) = withContext(Dispatchers.IO) { ticketRepo.getTicketById(id) }
    suspend fun getAllTickets() = withContext(Dispatchers.IO) { ticketRepo.getAllTickets() }

    // TICKET ITEM
    fun insertTicketItem(item: TicketItem, ticketId: UUID, onResult: (Boolean) -> Unit) = viewModelScope.launch {
        val result = withContext(Dispatchers.IO) { ticketItemRepo.insertItem(item, ticketId) }
        onResult(result)
    }
    suspend fun getTicketsItemByTicketId(id: UUID) = withContext(Dispatchers.IO) { ticketItemRepo.getItemsByTicketId(id) }

    // STATS
    suspend fun getCategoryStats(period: Period, periodOffset: Int = 0) = withContext(Dispatchers.IO) {
        statsRepo.getCategoryStats(period, periodOffset)
    }
    suspend fun getPeriodCategoryHistory(categoryName: String, period: Period, periodQuantity: Int) = withContext(Dispatchers.IO) { statsRepo.getPeriodCategoryHistory(categoryName, period, periodQuantity) }
    suspend fun getTicketsByFilters(categoryName: String, period: Period, periodQuantity: Int) = withContext(Dispatchers.IO) {
        val calendar = Calendar.getInstance()
        when (period) {
            Period.MENSUAL -> calendar.add(Calendar.MONTH, -periodQuantity)
            Period.SEMANAL -> calendar.add(Calendar.WEEK_OF_YEAR, -periodQuantity)
        }
        val minDate = calendar.time
        ticketRepo.getTicketsByFilters(categoryName = categoryName, minDate = minDate)
    }

}