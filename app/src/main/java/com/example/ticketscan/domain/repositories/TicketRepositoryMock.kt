package com.example.ticketscan.domain.repositories

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.ticketscan.domain.model.Store
import com.example.ticketscan.domain.model.Ticket
import com.example.ticketscan.domain.model.TicketItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.time.LocalDate
import java.util.UUID

class TicketRepositoryMock : TicketRepository {
    private val storage = mutableMapOf<UUID, Ticket>()
    private val mutex = Mutex()

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun getTicket(id: UUID): Flow<Ticket> {
        mutex.withLock {
            if (!storage.containsKey(id)) {
                storage[id] = createSampleTicket(id)
            }
        }
        return flow { emit(storage.getValue(id)) }
    }

    override suspend fun updateTicket(ticket: Ticket) {
        mutex.withLock {
            storage[ticket.id] = ticket
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createSampleTicket(id: UUID): Ticket {
        val items = listOf(
            TicketItem(
                id = UUID.randomUUID(),
                name = "Pan",
                category = "Alimentos",
                quantity = 2,
                isIntUnit = true,
                price = 7.0
            ),
            TicketItem(
                id = UUID.randomUUID(),
                name = "Leche",
                category = "Alimentos",
                quantity = 1,
                isIntUnit = true,
                price = 5.0
            ),
            TicketItem(
                id = UUID.randomUUID(),
                name = "Jabón",
                category = "Limpieza",
                quantity = 3,
                isIntUnit = true,
                price = 2.0
            )
        )
        return Ticket(
            id = id,
            date = LocalDate.of(2025, 5, 15),
            store = Store(id = UUID.randomUUID(), name = "Supermercado Ejemplo", cuit = 20304050607, location = "Calle Falsa 123"),
            items = items,
            total = items.sumOf { it.price * it.quantity }
        )
    }
}