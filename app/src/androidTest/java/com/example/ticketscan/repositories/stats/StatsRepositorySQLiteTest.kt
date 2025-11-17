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
import com.example.ticketscan.domain.repositories.icon.IconRepositorySQLite
import com.example.ticketscan.domain.model.Icon

@RunWith(AndroidJUnit4::class)
class StatsRepositorySQLiteTest {
    private lateinit var categoryRepo: CategoryRepositorySQLite
    private lateinit var repo: StatsRepositorySQLite
    private lateinit var context: Context

    @Before
    fun setup() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
        val iconRepository = IconRepositorySQLite(context)
        categoryRepo = CategoryRepositorySQLite(context, iconRepository)
        repo = StatsRepositorySQLite(context)
        val db = context.openOrCreateDatabase("ticketscan.db", Context.MODE_PRIVATE, null)
        db.execSQL("CREATE TABLE IF NOT EXISTS icons (id TEXT PRIMARY KEY, name TEXT NOT NULL, icon_key TEXT NOT NULL)")
        db.execSQL("CREATE TABLE IF NOT EXISTS tickets (id TEXT PRIMARY KEY, date TEXT NOT NULL, store_id TEXT NOT NULL, total REAL NOT NULL)")
        db.execSQL("CREATE TABLE IF NOT EXISTS categories (id TEXT PRIMARY KEY, name TEXT NOT NULL, color INTEGER NOT NULL, icon_id TEXT NOT NULL, is_active INTEGER NOT NULL DEFAULT 1)")
        db.execSQL("CREATE TABLE IF NOT EXISTS ticket_items (id TEXT PRIMARY KEY, ticket_id TEXT NOT NULL, name TEXT NOT NULL, category_id TEXT NOT NULL, quantity INTEGER NOT NULL, isIntUnit INTEGER NOT NULL, price REAL NOT NULL)")
        db.execSQL("CREATE TABLE IF NOT EXISTS stores (id TEXT PRIMARY KEY, name TEXT NOT NULL, cuit INTEGER NOT NULL, location TEXT NOT NULL)")
        // Create views for stats
        db.execSQL("CREATE VIEW IF NOT EXISTS vw_weekly_expenses AS SELECT c.name AS category_name, c.color AS category_color, strftime('%Y-%W', t.date) AS period, SUM(ti.price) AS total FROM ticket_items ti JOIN tickets t ON ti.ticket_id = t.id JOIN categories c ON c.id = ti.category_id GROUP BY category_name, category_color, period")
        db.execSQL("CREATE VIEW IF NOT EXISTS vw_monthly_expenses AS SELECT c.name AS category_name, c.color AS category_color, strftime('%Y-%m', t.date) AS period, SUM(ti.price) AS total FROM ticket_items ti JOIN tickets t ON ti.ticket_id = t.id JOIN categories c ON c.id = ti.category_id GROUP BY category_name, category_color, period")
        // Limpiar las tablas antes de cada test
        db.execSQL("DELETE FROM tickets")
        db.execSQL("DELETE FROM ticket_items")
        db.execSQL("DELETE FROM categories")
        db.execSQL("DELETE FROM stores")
        db.execSQL("DELETE FROM icons")
        db.close()
    }

    @Test
    fun testGetCategoryStatsEmpty() = runBlocking {
        val stats = repo.getCategoryStats(Period.SEMANAL)
        assertTrue(stats.isEmpty())
    }

    @Test
    fun testGetCategoryStatsWithData() = runBlocking {
        val icon = Icon.default()
        val db = context.openOrCreateDatabase("ticketscan.db", Context.MODE_PRIVATE, null)
        db.execSQL("INSERT OR IGNORE INTO icons (id, name, icon_key) VALUES (?, ?, ?)", arrayOf(icon.id.toString(), icon.name, icon.iconKey))
        db.close()
        val category = Category(UUID.randomUUID(), "TestCat", Color.Red, icon)
        categoryRepo.insertCategory(category)
        val db2 = context.openOrCreateDatabase("ticketscan.db", Context.MODE_PRIVATE, null)
        val ticketId = UUID.randomUUID().toString()
        // Insertar un store para cumplir la FK de tickets
        val storeId = UUID.randomUUID().toString()
        db2.execSQL("CREATE TABLE IF NOT EXISTS stores (id TEXT PRIMARY KEY, name TEXT NOT NULL, cuit INTEGER NOT NULL, location TEXT NOT NULL)")
        db2.execSQL("INSERT INTO stores (id, name, cuit, location) VALUES (?, ?, ?, ?)", arrayOf<Any>(storeId, "TestStore", 12345678901L, "TestLocation"))
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        db2.execSQL("INSERT INTO tickets (id, date, store_id, total) VALUES (?, ?, ?, ?)", arrayOf(ticketId, today, storeId, 20.0))
        db2.execSQL("INSERT INTO ticket_items (id, ticket_id, name, category_id, quantity, isIntUnit, price) VALUES (?, ?, ?, ?, ?, ?, ?)", arrayOf<Any>(UUID.randomUUID().toString(), ticketId, "TestItem", category.id.toString(), 2, 1, 10.0))
        db2.close()
        val stats = repo.getCategoryStats(Period.SEMANAL)
        // CategoryStat.name contains the category name, not the ID
        assertTrue(stats.any { it.name == category.name })
        // The view uses SUM(ti.price), not SUM(ti.quantity * ti.price), so it sums the price (10.0) not the total (20.0)
        assertTrue(stats.any { it.amount.toDouble() == 10.0 })
    }
}
