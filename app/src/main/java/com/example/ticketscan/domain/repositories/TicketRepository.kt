package com.example.ticketscan.domain.repositories

import com.example.ticketscan.ui.screens.Category

interface TicketRepository {
    suspend fun createTicket(): List<Category>
}
