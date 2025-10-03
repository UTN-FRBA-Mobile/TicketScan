package com.example.ticketscan.repositories.stats

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.ticketscan.domain.model.Category
import com.example.ticketscan.domain.repositories.category.CategoryRepositorySQLite
import com.example.ticketscan.domain.repositories.stats.StatsRepositorySQLite
import com.example.ticketscan.ui.components.Period
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import kotlinx.coroutines.runBlocking
import androidx.compose.ui.graphics.Color

@RunWith(AndroidJUnit4::class)
class StatsRepositorySQLiteTest {
    private lateinit var categoryRepo: CategoryRepositorySQLite
    private lateinit var repo: StatsRepositorySQLite
    private lateinit var context: Context

    @Before
    fun setup() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
        categoryRepo = CategoryRepositorySQLite(context)
        repo = StatsRepositorySQLite(context)
        val db = context.openOrCreateDatabase("ticketscan.db", Context.MODE_PRIVATE, null)
        db.execSQL("CREATE TABLE IF NOT EXISTS tickets (id TEXT PRIMARY KEY, date TEXT NOT NULL, store_id TEXT NOT NULL, total REAL NOT NULL)")
        db.execSQL("CREATE TABLE IF NOT EXISTS categories (id TEXT PRIMARY KEY, name TEXT NOT NULL, color INTEGER NOT NULL)")
        db.execSQL("CREATE TABLE IF NOT EXISTS ticket_items (id TEXT PRIMARY KEY, ticket_id TEXT NOT NULL, name TEXT NOT NULL, category_id TEXT NOT NULL, quantity INTEGER NOT NULL, isIntUnit INTEGER NOT NULL, price REAL NOT NULL)")
        db.execSQL("CREATE TABLE IF NOT EXISTS stores (id TEXT PRIMARY KEY, name TEXT NOT NULL, cuit INTEGER NOT NULL, location TEXT NOT NULL)")
        // Limpiar las tablas antes de cada test
        db.execSQL("DELETE FROM tickets")
        db.execSQL("DELETE FROM ticket_items")
        db.execSQL("DELETE FROM categories")
        db.execSQL("DELETE FROM stores")
        db.close()
    }

    @Test
    fun testGetCategoryStatsEmpty() = runBlocking {
        val stats = repo.getCategoryStats(Period.SEMANAL)
        assertTrue(stats.isEmpty())
    }

    @Test
    fun testGetCategoryStatsWithData() = runBlocking {
        val category = Category(UUID.randomUUID(), "TestCat", Color.Red)
        categoryRepo.insertCategory(category)
        val db = context.openOrCreateDatabase("ticketscan.db", Context.MODE_PRIVATE, null)
        val ticketId = UUID.randomUUID().toString()
        // Insertar un store para cumplir la FK de tickets
        val storeId = UUID.randomUUID().toString()
        db.execSQL("CREATE TABLE IF NOT EXISTS stores (id TEXT PRIMARY KEY, name TEXT NOT NULL, cuit INTEGER NOT NULL, location TEXT NOT NULL)")
        db.execSQL("INSERT INTO stores (id, name, cuit, location) VALUES (?, ?, ?, ?)", arrayOf<Any>(storeId, "TestStore", 12345678901L, "TestLocation"))
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        db.execSQL("INSERT INTO tickets (id, date, store_id, total) VALUES (?, ?, ?, ?)", arrayOf(ticketId, today, storeId, 20.0))
        db.execSQL("INSERT INTO ticket_items (id, ticket_id, name, category_id, quantity, isIntUnit, price) VALUES (?, ?, ?, ?, ?, ?, ?)", arrayOf<Any>(UUID.randomUUID().toString(), ticketId, "TestItem", category.id.toString(), 2, 1, 10.0))
        db.close()
        val stats = repo.getCategoryStats(Period.SEMANAL)
        assertTrue(stats.any { it.name == category.id.toString() })
        assertTrue(stats.any { it.amount.toDouble() == 20.0 })
    }
}
