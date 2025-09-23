package com.example.ticketscan.domain.repositories.stats

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.fromColorLong
import com.example.ticketscan.ui.components.CategoryStat
import com.example.ticketscan.ui.components.Period
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class StatsRepositorySQLite(context: Context) : StatsRepository {
    private val dbHelper = object : SQLiteOpenHelper(context, "ticketscan.db", null, 1) {
        override fun onCreate(db: SQLiteDatabase) {
            db.execSQL("""
                CREATE TABLE IF NOT EXISTS tickets (
                    id TEXT PRIMARY KEY,
                    date TEXT NOT NULL,
                    store_id TEXT NOT NULL,
                    total REAL NOT NULL
                );
            """)
            db.execSQL("""
                CREATE TABLE IF NOT EXISTS stores (
                    id TEXT PRIMARY KEY,
                    name TEXT NOT NULL,
                    cuit INTEGER NOT NULL,
                    location TEXT NOT NULL
                );
            """)
            db.execSQL("""
                CREATE TABLE IF NOT EXISTS categories (
                    id TEXT PRIMARY KEY,
                    name TEXT NOT NULL,
                    color INTEGER NOT NULL
                );
            """)
            db.execSQL("""
                CREATE TABLE IF NOT EXISTS ticket_items (
                    id TEXT PRIMARY KEY,
                    ticket_id TEXT NOT NULL,
                    name TEXT NOT NULL,
                    category_id TEXT NOT NULL,
                    quantity INTEGER NOT NULL,
                    isIntUnit INTEGER NOT NULL,
                    price REAL NOT NULL,
                    FOREIGN KEY(ticket_id) REFERENCES tickets(id),
                    FOREIGN KEY(category_id) REFERENCES categories(id)
                );
            """)
        }
        override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
            db.execSQL("DROP TABLE IF EXISTS ticket_items;")
            db.execSQL("DROP TABLE IF EXISTS tickets;")
            db.execSQL("DROP TABLE IF EXISTS stores;")
            db.execSQL("DROP TABLE IF EXISTS categories;")
            onCreate(db)
        }
    }

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
        db.close()
        return stats
    }
}
