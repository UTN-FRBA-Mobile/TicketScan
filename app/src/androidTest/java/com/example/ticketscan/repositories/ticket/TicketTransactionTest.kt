package com.example.ticketscan.repositories.ticket

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.ticketscan.domain.model.*
import com.example.ticketscan.domain.repositories.category.CategoryRepositorySQLite
import com.example.ticketscan.domain.repositories.store.StoreRepositorySQLite
import com.example.ticketscan.domain.repositories.ticket.TicketRepositorySQLite
import com.example.ticketscan.domain.repositories.ticketitem.TicketItemRepositorySQLite
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*
import androidx.compose.ui.graphics.Color
import com.example.ticketscan.domain.repositories.icon.IconRepositorySQLite
import com.example.ticketscan.domain.model.Icon

@RunWith(AndroidJUnit4::class)
class TicketTransactionTest {
    private lateinit var context: Context
    private lateinit var storeRepo: StoreRepositorySQLite
    private lateinit var categoryRepo: CategoryRepositorySQLite
    private lateinit var ticketItemRepo: TicketItemRepositorySQLite
    private lateinit var ticketRepo: TicketRepositorySQLite

    @Before
    fun setup() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
        storeRepo = StoreRepositorySQLite(context)
        val iconRepository = IconRepositorySQLite(context)
        categoryRepo = CategoryRepositorySQLite(context, iconRepository)
        ticketItemRepo = TicketItemRepositorySQLite(context, categoryRepo)
        ticketRepo = TicketRepositorySQLite(context, ticketItemRepo)

        val db = context.openOrCreateDatabase("ticketscan.db", Context.MODE_PRIVATE, null)
        // crear tablas si no existen y limpiar
        db.execSQL("CREATE TABLE IF NOT EXISTS icons (id TEXT PRIMARY KEY, name TEXT NOT NULL, icon_key TEXT NOT NULL)")
        db.execSQL("CREATE TABLE IF NOT EXISTS stores (id TEXT PRIMARY KEY, name TEXT NOT NULL, cuit INTEGER NOT NULL, location TEXT NOT NULL)")
        db.execSQL("CREATE TABLE IF NOT EXISTS categories (id TEXT PRIMARY KEY, name TEXT NOT NULL, color INTEGER NOT NULL, icon_id TEXT NOT NULL, is_active INTEGER NOT NULL DEFAULT 1)")
        db.execSQL("CREATE TABLE IF NOT EXISTS tickets (id TEXT PRIMARY KEY, date TEXT NOT NULL, store_id TEXT NOT NULL, total REAL NOT NULL)")
        db.execSQL("CREATE TABLE IF NOT EXISTS ticket_items (id TEXT PRIMARY KEY, ticket_id TEXT NOT NULL, name TEXT NOT NULL, category_id TEXT NOT NULL, quantity INTEGER NOT NULL, isIntUnit INTEGER NOT NULL, price REAL NOT NULL)")
        db.execSQL("DELETE FROM ticket_items")
        db.execSQL("DELETE FROM tickets")
        db.execSQL("DELETE FROM categories")
        db.execSQL("DELETE FROM stores")
        db.execSQL("DELETE FROM icons")
        db.close()
    }

    @Test
    fun testInsertTicket_withItemFailure_rollsBack() = runBlocking {
        // preparar datos: store y category
        val store = Store(UUID.randomUUID(), "S", 0L, "L")
        assertTrue(storeRepo.insertStore(store))
        val icon = Icon.default()
        val dbIcon = context.openOrCreateDatabase("ticketscan.db", Context.MODE_PRIVATE, null)
        dbIcon.execSQL("INSERT OR IGNORE INTO icons (id, name, icon_key) VALUES (?, ?, ?)", arrayOf(icon.id.toString(), icon.name, icon.iconKey))
        dbIcon.close()
        val category = Category(UUID.randomUUID(), "C", Color.Red, icon)
        assertTrue(categoryRepo.insertCategory(category))

        // crear ticket y items
        val ticketId = UUID.randomUUID()
        val failingItemId = UUID.randomUUID()
        val item = TicketItem(failingItemId, "I", category, 1, true, 10.0)
        val ticket = Ticket(id = ticketId, date = Date(), store = store, origin = TicketOrigin.TEXT, items = listOf(item), total = 10.0)

        // insertar previamente un registro en ticket_items con el mismo id para provocar fallo por PK
        val db = context.openOrCreateDatabase("ticketscan.db", Context.MODE_PRIVATE, null)
        val preId = failingItemId.toString()
        db.execSQL("INSERT INTO ticket_items (id, ticket_id, name, category_id, quantity, isIntUnit, price) VALUES (?, ?, ?, ?, ?, ?, ?)", arrayOf<Any>(preId, UUID.randomUUID().toString(), "X", category.id.toString(), 1, 1, 5.0))
        db.close()

        // intentar insertar el ticket (debe fallar debido al item duplicado) y verificar rollback
        val res = ticketRepo.insertTicket(ticket)
        assertFalse("insertTicket should fail when an item insertion fails", res)

        // verificar que el ticket no existe
        val got = ticketRepo.getTicketById(ticketId)
        assertNull("Ticket should not be present after rollback", got)

        // verificar que no hay items asociados al ticketId
        val items = ticketItemRepo.getItemsByTicketId(ticketId)
        assertTrue(items.isEmpty())
    }
}

