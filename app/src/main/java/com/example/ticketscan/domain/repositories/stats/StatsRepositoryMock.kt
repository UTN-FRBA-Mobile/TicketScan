package com.example.ticketscan.domain.repositories.stats

import com.example.ticketscan.domain.model.Ticket
import com.example.ticketscan.domain.repositories.ticket.TicketRepository
import com.example.ticketscan.ui.components.CategoryStat
import com.example.ticketscan.ui.components.Period
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class StatsRepositoryMock(private val ticketRepository: TicketRepository) : StatsRepository {

    override suspend fun getCategoryStats(period: Period): List<CategoryStat> {
        val tickets = ticketRepository.getAllTickets()
        val now = Calendar.getInstance()
        val startDate = (now.clone() as Calendar).apply {
            when (period) {
                Period.MENSUAL -> add(Calendar.MONTH, -1)
                Period.SEMANAL -> add(Calendar.WEEK_OF_YEAR, -1)
            }
        }.time

        return tickets
            .filter { it.date.after(startDate) }
            .flatMap { it.items }
            .groupBy { it.category }
            .map { (category, items) ->
                CategoryStat(
                    name = category.name,
                    amount = items.sumOf { it.price * it.quantity }.toBigDecimal(),
                    color = category.color
                )
            }
    }

    override suspend fun getMonthlyCategoryHistory(categoryName: String): List<MonthlyExpense> {
        val tickets = ticketRepository.getTicketsByCategory(categoryName)
        val monthFormat = SimpleDateFormat("MMM", Locale.getDefault())
        val now = Calendar.getInstance()

        // Creamos un mapa para los ultimos 4 meses con valor inicial 0
        val monthlyTotals = (0..3).associate {
            val monthCalendar = (now.clone() as Calendar).apply { add(Calendar.MONTH, -it) }
            val monthKey = monthFormat.format(monthCalendar.time)
            monthKey to BigDecimal.ZERO
        }.toMutableMap()

        // Agrupamos los tickets por mes y sumamos sus totales
        tickets.forEach { ticket ->
            val ticketCalendar = Calendar.getInstance().apply { time = ticket.date }
            if (now.get(Calendar.YEAR) == ticketCalendar.get(Calendar.YEAR) && now.get(Calendar.MONTH) - ticketCalendar.get(Calendar.MONTH) < 4) {
                val monthKey = monthFormat.format(ticket.date)
                monthlyTotals[monthKey] = monthlyTotals.getOrDefault(monthKey, BigDecimal.ZERO).add(ticket.total.toBigDecimal())
            }
        }

        return monthlyTotals.entries.map { MonthlyExpense(it.key, it.value) }.reversed()
    }

    override suspend fun getTransactionsForCategory(
        categoryName: String,
        period: Period
    ): List<Ticket> {
        return ticketRepository.getTicketsByCategory(categoryName)
    }
}