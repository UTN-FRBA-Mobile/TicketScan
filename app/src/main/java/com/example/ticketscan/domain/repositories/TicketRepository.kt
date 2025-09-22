package com.example.ticketscan.domain.repositories

import com.example.ticketscan.domain.model.Ticket
import kotlinx.coroutines.flow.Flow

interface TicketRepository {
    suspend fun processTicket(): Flow<Ticket>
}
