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

        val (viewName, periodValue) = when (period) {
            Period.MENSUAL -> {
                calendar.add(Calendar.MONTH, -periodOffset)
                val format = SimpleDateFormat("yyyy-MM", Locale.getDefault())
                Pair("vw_monthly_expenses", format.format(calendar.time))
            }
            Period.SEMANAL -> {
                calendar.add(Calendar.WEEK_OF_YEAR, -periodOffset)
                val format = SimpleDateFormat("yyyy-WW", Locale.getDefault())
                Pair("vw_weekly_expenses", format.format(calendar.time))
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

    override suspend fun getPeriodCategoryHistory(categoryName: String, period: Period, periodQuantity: Int): List<PeriodExpense> {
        val db = dbHelper.readableDatabase
        val periodExpenses = mutableListOf<PeriodExpense>()

        val (viewName, periodLabelFormat, dbPeriodFormat) = when (period) {
            Period.MENSUAL -> Triple("vw_monthly_expenses", SimpleDateFormat("MMM", Locale.forLanguageTag("es-ES")), SimpleDateFormat("yyyy-MM", Locale.getDefault()))
            Period.SEMANAL -> Triple("vw_weekly_expenses", SimpleDateFormat("'S'w", Locale.getDefault()), SimpleDateFormat("yyyy-WW", Locale.getDefault()))
        }

        val cursor = db.rawQuery(
            "SELECT period, total FROM $viewName WHERE category_name = ? ORDER BY period DESC LIMIT ?",
            arrayOf(categoryName, periodQuantity.toString())
        )

        while (cursor.moveToNext()) {
            val periodStr = cursor.getString(cursor.getColumnIndexOrThrow("period"))
            val total = cursor.getDouble(cursor.getColumnIndexOrThrow("total"))

            val date = dbPeriodFormat.parse(periodStr)
            val shortPeriodName = date?.let { periodLabelFormat.format(it) } ?: ""

            periodExpenses.add(PeriodExpense(shortPeriodName, BigDecimal.valueOf(total)))
        }
        cursor.close()
        return periodExpenses.reversed()
    }
}
