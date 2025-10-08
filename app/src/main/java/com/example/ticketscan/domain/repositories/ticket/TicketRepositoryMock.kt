package com.example.ticketscan.domain.repositories.ticket

import android.os.Build
import com.example.ticketscan.domain.model.Ticket
import com.example.ticketscan.domain.model.TicketItem
import com.example.ticketscan.domain.model.Store
import com.example.ticketscan.domain.model.Category
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.toList
import java.util.UUID

class TicketRepositoryMock : TicketRepository {
    override suspend fun processTicket(): Flow<Ticket> {
        val alimentosCategory = Category(
            id = UUID.nameUUIDFromBytes("Alimentos".toByteArray()),
            name = "Alimentos",
            color = Color(0xFF2196F3)
        )
        val limpiezaCategory = Category(
            id = UUID.nameUUIDFromBytes("Limpieza".toByteArray()),
            name = "Limpieza",
            color = Color(0xFF4CAF50)
        )
        val items = listOf(
            TicketItem(
                id = UUID.randomUUID(),
                name = "Pan",
                category = alimentosCategory,
                quantity = 2,
                isIntUnit = true,
                price = 7.0
            ),
            TicketItem(
                id = UUID.randomUUID(),
                name = "Leche",
                category = alimentosCategory,
                quantity = 1,
                isIntUnit = true,
                price = 5.0
            ),
            TicketItem(
                id = UUID.randomUUID(),
                name = "Jabón",
                category = limpiezaCategory,
                quantity = 3,
                isIntUnit = true,
                price = 2.0
            )
        )
        val ticket = Ticket(
            id = UUID.randomUUID(),
            date = java.util.Date(),
            store = Store(id = UUID.randomUUID(), name = "Supermercado Ejemplo", cuit = 20304050607, location = "Calle Falsa 123"),
            items = items,
            total = items.sumOf { it.price * it.quantity }
        )
        return flow { emit(ticket) }
    }

    override suspend fun getTicketById(id: UUID): Ticket? {
        return processTicket().firstOrNull { it.id == id }
    }

    override suspend fun getAllTickets(): List<Ticket> {
        return processTicket().toList()
    }

    override suspend fun insertTicket(ticket: Ticket): Boolean {
        // Mock: siempre retorna true
        return true
    }

    override suspend fun updateTicket(ticket: Ticket): Boolean {
        // Mock: siempre retorna true
        return true
    }

    override suspend fun deleteTicket(id: UUID): Boolean {
        // Mock: siempre retorna true
        return true
    }
}