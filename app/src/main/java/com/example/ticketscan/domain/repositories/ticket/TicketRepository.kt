package com.example.ticketscan.domain.repositories.ticket

import com.example.ticketscan.domain.model.Ticket
import java.util.UUID

interface TicketRepository {
    suspend fun getTicketById(id: UUID): Ticket?
    suspend fun getAllTickets(): List<Ticket>
    suspend fun insertTicket(ticket: Ticket): Boolean
    suspend fun updateTicket(ticket: Ticket): Boolean
    suspend fun deleteTicket(id: UUID): Boolean
    suspend fun getTicketsByCategory(categoryName: String): List<Ticket>
}