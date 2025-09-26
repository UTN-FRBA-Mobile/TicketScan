package com.example.ticketscan.domain.repositories

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.ticketscan.domain.model.Store
import com.example.ticketscan.domain.model.Ticket
import com.example.ticketscan.domain.model.TicketItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.time.LocalDate
import java.util.UUID

class TicketRepositoryMock : TicketRepository {
    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun getTicket(id: UUID): Flow<Ticket> {
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
        val ticket = Ticket(
            id = id,
            date = LocalDate.of(2025, 5, 15),
            store = Store(id = UUID.randomUUID(), name = "Supermercado Ejemplo", cuit = 20304050607, location = "Calle Falsa 123"),
            items = items,
            total = items.sumOf { it.price * it.quantity }
        )
        return flow { emit(ticket) }
    }
}