package com.example.ticketscan.repositories.ticket

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.ticketscan.domain.model.Store
import com.example.ticketscan.domain.model.Category
import com.example.ticketscan.domain.model.Ticket
import com.example.ticketscan.domain.model.TicketItem
import com.example.ticketscan.domain.repositories.category.CategoryRepositorySQLite
import com.example.ticketscan.domain.repositories.store.StoreRepositorySQLite
import com.example.ticketscan.domain.repositories.ticketitem.TicketItemRepositorySQLite
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.UUID
import kotlinx.coroutines.runBlocking
import androidx.compose.ui.graphics.Color
import com.example.ticketscan.domain.model.TicketOrigin
import com.example.ticketscan.domain.repositories.ticket.TicketRepositorySQLite
import com.example.ticketscan.domain.repositories.icon.IconRepositorySQLite
import com.example.ticketscan.domain.model.Icon
import java.util.Date

@RunWith(AndroidJUnit4::class)
class TicketRepositorySQLiteTest {
    private lateinit var categoryRepo: CategoryRepositorySQLite
    private lateinit var storeRepo: StoreRepositorySQLite
    private lateinit var ticketItemRepo: TicketItemRepositorySQLite
    private lateinit var repo: TicketRepositorySQLite
    private lateinit var context: Context

    @Before
    fun setup() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
        val iconRepository = IconRepositorySQLite(context)
        categoryRepo = CategoryRepositorySQLite(context, iconRepository)
        storeRepo = StoreRepositorySQLite(context)
        ticketItemRepo = TicketItemRepositorySQLite(context, categoryRepo)
        repo = TicketRepositorySQLite(context, ticketItemRepo)
        val db = context.openOrCreateDatabase("ticketscan.db", Context.MODE_PRIVATE, null)
        db.execSQL("CREATE TABLE IF NOT EXISTS icons (id TEXT PRIMARY KEY, name TEXT NOT NULL, icon_key TEXT NOT NULL)")
        db.execSQL("CREATE TABLE IF NOT EXISTS stores (id TEXT PRIMARY KEY, name TEXT NOT NULL, cuit INTEGER NOT NULL, location TEXT NOT NULL)")
        db.execSQL("CREATE TABLE IF NOT EXISTS categories (id TEXT PRIMARY KEY, name TEXT NOT NULL, color INTEGER NOT NULL, icon_id TEXT NOT NULL, is_active INTEGER NOT NULL DEFAULT 1)")
        db.execSQL("CREATE TABLE IF NOT EXISTS tickets (id TEXT PRIMARY KEY, date TEXT NOT NULL, store_id TEXT, total REAL NOT NULL)")
        db.execSQL("CREATE TABLE IF NOT EXISTS ticket_items (id TEXT PRIMARY KEY, ticket_id TEXT NOT NULL, name TEXT NOT NULL, category_id TEXT NOT NULL, quantity INTEGER NOT NULL, isIntUnit INTEGER NOT NULL, price REAL NOT NULL)")
        db.close()
    }

    @Test
    fun testInsertAndGetTicket() = runBlocking {
        val store = Store(UUID.randomUUID(), "TestStore", 12345678901L, "TestLocation")
        storeRepo.insertStore(store)
        val icon = Icon.default()
        val db = context.openOrCreateDatabase("ticketscan.db", Context.MODE_PRIVATE, null)
        db.execSQL("INSERT OR IGNORE INTO icons (id, name, icon_key) VALUES (?, ?, ?)", arrayOf(icon.id.toString(), icon.name, icon.iconKey))
        db.close()
        val category = Category(UUID.randomUUID(), "TestCat", Color.Red, icon)
        categoryRepo.insertCategory(category)
        val ticketId = UUID.randomUUID()
        val item = TicketItem(UUID.randomUUID(), "TestItem", category, 2, true, 10.0)
        // No insertar el ticket directamente en la base de datos
        // Insertar solo el ticket usando el repositorio
        val ticket = Ticket(id = ticketId, date = Date(), store = store, origin = TicketOrigin.TEXT, items = listOf(item), total = 20.0)
        assertTrue(repo.insertTicket(ticket))
        val result = repo.getTicketById(ticket.id)
        assertNotNull(result)
        assertEquals(ticket.store?.name, result?.store?.name)
        assertEquals(ticket.total, result?.total)
    }
}
