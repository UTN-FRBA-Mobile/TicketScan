package com.example.ticketscan.domain.repositories.ticket

import android.content.Context
import androidx.compose.ui.graphics.Color
import com.example.ticketscan.domain.model.Category
import com.example.ticketscan.domain.model.Store
import com.example.ticketscan.domain.model.Ticket
import com.example.ticketscan.domain.model.TicketItem
import com.example.ticketscan.domain.model.TicketOrigin
import com.example.ticketscan.domain.repositories.ticketitem.TicketItemRepository
import kotlinx.coroutines.flow.Flow
import java.util.Calendar
import java.util.Date
import java.util.UUID

class TicketRepositoryMock (
    private val context: Context,
    private val ticketItemRepository: TicketItemRepository,
    override val ticketsChanged: Flow<Unit>
) : TicketRepository {

    private val mockTickets by lazy { generateMockTickets() }

    private val categories = listOf(
        Category(UUID.randomUUID(), "Alimentación", Color(red = 0, green = 150, blue = 136, alpha = 255)),
        Category(UUID.randomUUID(), "Transporte", Color(red = 33, green = 150, blue = 243, alpha = 255)),
        Category(UUID.randomUUID(), "Entretenimiento", Color(0xFFFF9800)),
        Category(UUID.randomUUID(), "Salud", Color(0xFFF44336)),
        Category(UUID.randomUUID(), "Hogar", Color(0xFF9C27B0)),
        Category(UUID.randomUUID(), "Otros", Color.Gray)
    )

    private fun generateMockTickets(): List<Ticket> {
        val alimentacion = categories.first { it.name == "Alimentación" }
        val transporte = categories.first { it.name == "Transporte" }
        val salud = categories.first { it.name == "Salud" }
        val entretenimiento = categories.first { it.name == "Entretenimiento" }
        val otros = categories.first { it.name == "Otros" }

        return listOf(
            // Octubre
            Ticket(UUID.randomUUID(), getDate(2025, 10, 12), Store(UUID.randomUUID(), "Super Coto", 1L, "Av. Rivadavia 1234"),
                TicketOrigin.MEDIA, listOf(TicketItem(UUID.randomUUID(), "Leche, Pan, Huevos", alimentacion, 1, false, 4500.0)), 4500.0),
            Ticket(UUID.randomUUID(), getDate(2025, 10, 10), Store(UUID.randomUUID(), "Farmacity", 2L, "Av. Corrientes 5678"),
                TicketOrigin.MEDIA, listOf(TicketItem(UUID.randomUUID(), "Ibuprofeno", salud, 2, true, 1200.0)), 2400.0),
            Ticket(UUID.randomUUID(), getDate(2025, 10, 2), Store(UUID.randomUUID(), "Subte", 4L, "Estación Carlos Pellegrini"),
                TicketOrigin.MEDIA, listOf(TicketItem(UUID.randomUUID(), "Carga SUBE", transporte, 1, false, 2000.0)), 2000.0),

            // Septiembre
            Ticket(UUID.randomUUID(), getDate(2025, 9, 15), Store(UUID.randomUUID(), "Super Coto", 1L, "Av. Rivadavia 1234"),
                TicketOrigin.MEDIA, listOf(TicketItem(UUID.randomUUID(), "Leche, Pan, Huevos", alimentacion, 1, false, 4500.0)), 4500.0),
            Ticket(UUID.randomUUID(), getDate(2025, 9, 12), Store(UUID.randomUUID(), "Farmacity", 2L, "Av. Corrientes 5678"),
                TicketOrigin.MEDIA, listOf(TicketItem(UUID.randomUUID(), "Ibuprofeno", salud, 2, true, 1200.0)), 2400.0),
            Ticket(UUID.randomUUID(), getDate(2025, 9, 2), Store(UUID.randomUUID(), "Subte", 4L, "Estación Carlos Pellegrini"),
                TicketOrigin.MEDIA, listOf(TicketItem(UUID.randomUUID(), "Carga SUBE", transporte, 1, false, 2000.0)), 2000.0),

            // Agosto
            Ticket(UUID.randomUUID(), getDate(2025, 8, 20), Store(UUID.randomUUID(), "Parrilla Don Julio", 3L, "Guatemala 4691"),
                TicketOrigin.MEDIA, listOf(TicketItem(UUID.randomUUID(), "Cena para dos", alimentacion, 1, true, 28000.0)), 28000.0),
            Ticket(UUID.randomUUID(), getDate(2025, 8, 5), Store(UUID.randomUUID(), "Shell", 4L, "Av. del Libertador 400"),
                TicketOrigin.MEDIA, listOf(TicketItem(UUID.randomUUID(), "Nafta V-Power", transporte, 30, false, 900.0)), 27000.0),
            Ticket(UUID.randomUUID(), getDate(2025, 8, 15), Store(UUID.randomUUID(), "Cine Hoyts", 6L, "Shopping Abasto"),
                TicketOrigin.MEDIA, listOf(TicketItem(UUID.randomUUID(), "Entradas Avengers 5", entretenimiento, 2, true, 8000.0)), 16000.0),

            // Julio
            Ticket(UUID.randomUUID(), getDate(2025, 7, 25), Store(UUID.randomUUID(), "Super Coto", 1L, "Av. Rivadavia 1234"),
                TicketOrigin.MEDIA, listOf(TicketItem(UUID.randomUUID(), "Aceite, Arroz", alimentacion, 3, true, 2200.0)), 6600.0),
            Ticket(UUID.randomUUID(), getDate(2025, 7, 10), Store(UUID.randomUUID(), "Pizzeria Guerrin", 3L, "Av. Corrientes 1368"),
                TicketOrigin.MEDIA, listOf(TicketItem(UUID.randomUUID(), "Pizza grande muzzarella", alimentacion, 1, true, 9500.0)), 9500.0),

            // Junio
            Ticket(UUID.randomUUID(), getDate(2025, 6, 18), Store(UUID.randomUUID(), "Easy", 5L, "Av. Santa Fe 3000"),
                TicketOrigin.MEDIA, listOf(TicketItem(UUID.randomUUID(), "Lamparita LED", otros, 4, true, 1200.0)), 4800.0)
        )
    }

    private fun getDate(year: Int, month: Int, day: Int): Date {
        return Calendar.getInstance().apply { set(year, month - 1, day) }.time
    }

    override suspend fun getTicketById(id: UUID): Ticket? = mockTickets.find { it.id == id }

    override suspend fun getAllTickets(limit: Int?): List<Ticket> {
        if (limit != null) return mockTickets.take(limit)
        return mockTickets
    }

    override suspend fun getTicketsByFilters(categoryName: String?, minDate: Date?): List<Ticket> {
        return mockTickets.filter { ticket ->
            ticket.items.any { item ->
                (categoryName == null || item.category.name == categoryName) &&
                (minDate == null || ticket.date >= minDate) }
        }
    }

    override suspend fun insertTicket(ticket: Ticket): Boolean = true // No-op

    override suspend fun updateTicket(ticket: Ticket): Boolean = true // No-op

    override suspend fun deleteTicket(id: UUID): Boolean = true // No-op
}
