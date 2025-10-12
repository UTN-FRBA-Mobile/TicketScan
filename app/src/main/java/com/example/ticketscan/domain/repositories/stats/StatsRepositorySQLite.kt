package com.example.ticketscan.domain.repositories.stats

import android.content.Context
import com.example.ticketscan.data.database.DatabaseHelper
import androidx.compose.ui.graphics.Color
import com.example.ticketscan.domain.model.Ticket
import com.example.ticketscan.domain.repositories.category.CategoryRepository
import com.example.ticketscan.domain.repositories.ticket.TicketRepository
import com.example.ticketscan.ui.components.CategoryStat
import com.example.ticketscan.ui.components.Period
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class StatsRepositorySQLite(
    context: Context,
    private val ticketRepository: TicketRepository,
    private val categoryRepository: CategoryRepository,
) : StatsRepository {
    private val dbHelper = DatabaseHelper(context)

    override suspend fun getCategoryStats(period: Period): List<CategoryStat> {
        val db = dbHelper.readableDatabase
        val stats = mutableListOf<CategoryStat>()
        val now = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        when (period) {
            Period.SEMANAL -> now.add(Calendar.DAY_OF_YEAR, -7)
            Period.MENSUAL -> now.add(Calendar.MONTH, -1)
        }
        val minDate = dateFormat.format(now.time)

        val cursor = db.rawQuery(
            "SELECT c.name, SUM(ti.price * ti.quantity) as total, c.color FROM ticket_items ti " +
                    "JOIN tickets t ON ti.ticket_id = t.id " +
                    "JOIN categories c ON c.id = ti.category_id " +
                    "WHERE t.date >= ? GROUP BY c.name, c.color",
            arrayOf(minDate)
        )
        while (cursor.moveToNext()) {
            val categoryName = cursor.getString(cursor.getColumnIndexOrThrow("name"))
            val total = cursor.getDouble(cursor.getColumnIndexOrThrow("total"))
            val dbColor = cursor.getInt(cursor.getColumnIndexOrThrow("color"))
            val color = Color(dbColor)
            stats.add(CategoryStat(categoryName, BigDecimal.valueOf(total), color))
        }
        cursor.close()
        return stats
    }

    override suspend fun getMonthlyCategoryHistory(categoryName: String): List<MonthlyExpense> {
        val db = dbHelper.readableDatabase
        val monthlyExpenses = mutableListOf<MonthlyExpense>()

        val cursor = db.rawQuery(
            "SELECT strftime('%Y-%m', t.date) as month, SUM(ti.price * ti.quantity) as total " +
                    "FROM ticket_items ti " +
                    "JOIN tickets t ON ti.ticket_id = t.id " +
                    "JOIN categories c ON c.id = ti.category_id " +
                    "WHERE c.name = ? " +
                    "GROUP BY month " +
                    "ORDER BY month DESC",
            arrayOf(categoryName)
        )

        while (cursor.moveToNext()) {
            val month = cursor.getString(cursor.getColumnIndexOrThrow("month"))
            val total = cursor.getDouble(cursor.getColumnIndexOrThrow("total"))
            monthlyExpenses.add(MonthlyExpense(month, BigDecimal.valueOf(total)))
        }
        cursor.close()
        return monthlyExpenses
    }

    override suspend fun getTransactionsForCategory(categoryName: String, period: Period): List<Ticket> {
        val allTickets = ticketRepository.getAllTickets()
        val category = categoryRepository.getAllCategories().find { it.name == categoryName } ?: return emptyList()

        val now = Calendar.getInstance()
        when (period) {
            Period.SEMANAL -> now.add(Calendar.DAY_OF_YEAR, -7)
            Period.MENSUAL -> now.add(Calendar.MONTH, -1)
        }
        val minDate = now.time

        return allTickets.filter { ticket ->
            ticket.date.after(minDate) && ticket.items.any { item -> item.category.id == category.id }
        }
    }
}
