package com.example.ticketscan.domain.repositories

import com.example.ticketscan.domain.model.Ticket
import kotlinx.coroutines.flow.Flow
import java.util.UUID

interface TicketRepository {
    suspend fun createTextTicket(): Flow<Ticket>
    suspend fun getTicket(id: UUID): Flow<Ticket>
    suspend fun updateTicket(ticket: Ticket)
    suspend fun listTickets(): Flow<List<Ticket>>
}
