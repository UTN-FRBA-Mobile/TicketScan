package com.example.ticketscan.domain.repositories.ticket

import com.example.ticketscan.domain.model.Ticket
import kotlinx.coroutines.flow.Flow
import java.util.Date
import java.util.UUID

interface TicketRepository {
    val ticketsChanged: Flow<Unit>
    suspend fun getTicketById(id: UUID): Ticket?
    suspend fun getAllTickets(limit: Int? = null): List<Ticket>
    suspend fun insertTicket(ticket: Ticket): Boolean
    suspend fun updateTicket(ticket: Ticket): Boolean
    suspend fun deleteTicket(id: UUID): Boolean
    suspend fun getTicketsByFilters(categoryName: String? = null, minDate: Date? = null): List<Ticket>
}
