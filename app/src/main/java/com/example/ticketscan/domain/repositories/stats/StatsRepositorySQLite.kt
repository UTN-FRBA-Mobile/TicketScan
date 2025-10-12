package com.example.ticketscan.domain.repositories.stats

import android.content.Context
import com.example.ticketscan.data.database.DatabaseHelper
import androidx.compose.ui.graphics.Color
import com.example.ticketscan.domain.model.CategoryStat
import com.example.ticketscan.ui.components.Period
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class StatsRepositorySQLite(context: Context) : StatsRepository {
    private val dbHelper = DatabaseHelper.getInstance(context)

    override suspend fun getCategoryStats(period: Period, periodOffset: Int): List<CategoryStat> {
        val db = dbHelper.readableDatabase
        val stats = mutableListOf<CategoryStat>()
        val calendar = Calendar.getInstance()

        val (viewName, periodFormat, periodValue) = when (period) {
            Period.MENSUAL -> {
                calendar.add(Calendar.MONTH, -periodOffset)
                val format = SimpleDateFormat("yyyy-MM", Locale.getDefault())
                Triple("vw_monthly_expenses", format, format.format(calendar.time))
            }
            Period.SEMANAL -> {
                calendar.add(Calendar.WEEK_OF_YEAR, -periodOffset)
                val format = SimpleDateFormat("yyyy-WW", Locale.getDefault())
                Triple("vw_weekly_expenses", format, format.format(calendar.time))
            }
        }

        val query = "SELECT category_name, total, category_color FROM $viewName WHERE period = ?"
        val cursor = db.rawQuery(query, arrayOf(periodValue))

        while (cursor.moveToNext()) {
            val categoryName = cursor.getString(cursor.getColumnIndexOrThrow("category_name"))
            val total = cursor.getDouble(cursor.getColumnIndexOrThrow("total"))
            val colorInt = cursor.getInt(cursor.getColumnIndexOrThrow("category_color"))
            stats.add(CategoryStat(categoryName, BigDecimal.valueOf(total), Color(colorInt)))
        }
        cursor.close()
        return stats
    }

    override suspend fun getMonthlyCategoryHistory(categoryName: String, periodQuantity: Int): List<MonthlyExpense> {
        val db = dbHelper.readableDatabase
        val monthlyExpenses = mutableListOf<MonthlyExpense>()
        val monthFormat = SimpleDateFormat("MMM", Locale.forLanguageTag("es-ES"))
        val yearMonthFormat = SimpleDateFormat("yyyy-MM", Locale.getDefault())

        val cursor = db.rawQuery(
            "SELECT period, total FROM vw_monthly_expenses WHERE category_name = ? ORDER BY period DESC LIMIT ?",
            arrayOf(categoryName, periodQuantity.toString())
        )

        while (cursor.moveToNext()) {
            val monthStr = cursor.getString(cursor.getColumnIndexOrThrow("period"))
            val total = cursor.getDouble(cursor.getColumnIndexOrThrow("total"))

            val date = yearMonthFormat.parse(monthStr)
            val shortMonthName = date?.let { monthFormat.format(it) } ?: ""

            monthlyExpenses.add(MonthlyExpense(shortMonthName, BigDecimal.valueOf(total)))
        }
        cursor.close()
        return monthlyExpenses.reversed()
    }
}
