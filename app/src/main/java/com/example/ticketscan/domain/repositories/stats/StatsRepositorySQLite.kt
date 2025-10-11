package com.example.ticketscan.domain.repositories.stats

import android.content.Context
import com.example.ticketscan.data.database.DatabaseHelper
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.fromColorLong
import com.example.ticketscan.ui.components.CategoryStat
import com.example.ticketscan.ui.components.Period
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class StatsRepositorySQLite(context: Context) : StatsRepository {
    private val dbHelper = DatabaseHelper.getInstance(context)

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
            "SELECT ti.category_id, SUM(ti.price * ti.quantity) as total, c.color FROM ticket_items ti " +
            "JOIN tickets t ON ti.ticket_id = t.id " +
            "JOIN categories c ON c.id = ti.category_id " +
            "WHERE t.date >= ? GROUP BY ti.category_id",
            arrayOf(minDate)
        )
        while (cursor.moveToNext()) {
            val categoryId = cursor.getString(cursor.getColumnIndexOrThrow("category_id"))
            val total = cursor.getDouble(cursor.getColumnIndexOrThrow("total"))
            val dbColor = cursor.getInt(cursor.getColumnIndexOrThrow("color"))
            val color = Color.fromColorLong(dbColor.toLong())
            stats.add(CategoryStat(categoryId, BigDecimal.valueOf(total), color))
        }
        cursor.close()
        return stats
    }
}
