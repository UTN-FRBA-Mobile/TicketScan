package com.example.ticketscan.domain.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.ticketscan.domain.repositories.category.CategoryRepositorySQLite
import com.example.ticketscan.domain.repositories.store.StoreRepositorySQLite
import com.example.ticketscan.domain.repositories.ticket.TicketRepositorySQLite
import com.example.ticketscan.domain.repositories.ticketitem.TicketItemRepositorySQLite
import com.example.ticketscan.domain.repositories.stats.StatsRepositorySQLite

class RepositoryViewModelFactory(
    private val context: Context,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RepositoryViewModel::class.java)) {
            val categoryRepository = CategoryRepositorySQLite(context)
            val ticketItemRepository = TicketItemRepositorySQLite(context, categoryRepository)
            val storeRepository = StoreRepositorySQLite(context)
            val ticketRepository = TicketRepositorySQLite(context, storeRepository, ticketItemRepository)
            val statsRepository = StatsRepositorySQLite(context)

            @Suppress("UNCHECKED_CAST")
            return RepositoryViewModel(
                storeRepository,
                categoryRepository,
                ticketRepository,
                ticketItemRepository,
                statsRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

