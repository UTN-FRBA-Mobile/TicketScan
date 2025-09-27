package com.example.ticketscan.domain.repositories

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.ticketscan.domain.model.Category
import com.example.ticketscan.domain.model.Store
import com.example.ticketscan.domain.model.Ticket
import com.example.ticketscan.domain.model.TicketItem
import com.example.ticketscan.domain.model.TicketOrigin
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.time.LocalDate
import java.util.UUID

@RequiresApi(Build.VERSION_CODES.O)
object TicketRepositoryMock : TicketRepository {
    private val storage = mutableMapOf<UUID, Ticket>()
    private val mutex = Mutex()

    init {
        val ids = listOf(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID())
        ids.forEachIndexed { idx, id ->
            storage[id] = createSampleTicket(id)
        }
    }

    override suspend fun createTextTicket(): Flow<Ticket> {
        mutex.withLock {
            val newTicket = Ticket(
                id = UUID.randomUUID(),
                date = LocalDate.of(2025, 5, 15),
                items = listOf(),
                total = 0.0,
                store = null,
                origin = TicketOrigin.TEXT
            )
            storage[newTicket.id] = newTicket
            return flow { emit(newTicket) }
        }
    }

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

    override suspend fun listTickets(): Flow<List<Ticket>> {
       return flow { emit( storage.values.toList()) }
    }

    private fun createSampleTicket(id: UUID): Ticket {
        val items = listOf(
            TicketItem(
                id = UUID.randomUUID(),
                name = "Pan",
                category = Category.fromName("Alimentos"),
                quantity = 2,
                isIntUnit = true,
                price = 7.0
            ),
            TicketItem(
                id = UUID.randomUUID(),
                name = "Leche",
                category = Category.fromName("Alimentos"),
                quantity = 1,
                isIntUnit = true,
                price = 5.0
            ),
            TicketItem(
                id = UUID.randomUUID(),
                name = "Jabón",
                category = Category.fromName("Limpieza"),
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
            total = items.sumOf { it.price }
        )
    }
}