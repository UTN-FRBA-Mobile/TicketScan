package com.example.ticketscan.repositories.ticketitem

import android.content.ContentValues
import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.ticketscan.domain.model.Category
import com.example.ticketscan.domain.model.TicketItem
import com.example.ticketscan.domain.repositories.category.CategoryRepositorySQLite
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.UUID
import kotlinx.coroutines.runBlocking
import androidx.compose.ui.graphics.Color
import com.example.ticketscan.domain.repositories.ticketitem.TicketItemRepositorySQLite

@RunWith(AndroidJUnit4::class)
class TicketItemRepositorySQLiteTest {
    private lateinit var categoryRepo: CategoryRepositorySQLite
    private lateinit var repo: TicketItemRepositorySQLite
    private lateinit var context: Context

    @Before
    fun setup() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
        categoryRepo = CategoryRepositorySQLite(context)
        repo = TicketItemRepositorySQLite(context, categoryRepo)
        val db = context.openOrCreateDatabase("ticketscan.db", Context.MODE_PRIVATE, null)
        db.execSQL("CREATE TABLE IF NOT EXISTS categories (id TEXT PRIMARY KEY, name TEXT NOT NULL, color INTEGER NOT NULL)")
        db.execSQL("CREATE TABLE IF NOT EXISTS tickets (id TEXT PRIMARY KEY, date TEXT NOT NULL, store_id TEXT NOT NULL, total REAL NOT NULL)")
        db.execSQL("CREATE TABLE IF NOT EXISTS ticket_items (id TEXT PRIMARY KEY, ticket_id TEXT NOT NULL, name TEXT NOT NULL, category_id TEXT NOT NULL, quantity INTEGER NOT NULL, isIntUnit INTEGER NOT NULL, price REAL NOT NULL)")
        db.close()
    }

    @Test
    fun testInsertAndGetTicketItem() = runBlocking {
        val category = Category(UUID.randomUUID(), "TestCat", Color.Red)
        categoryRepo.insertCategory(category)
        val ticketId = UUID.randomUUID()
        val item = TicketItem(UUID.randomUUID(), "TestItem", category, 2, true, 10.0)
        val db = context.openOrCreateDatabase("ticketscan.db", Context.MODE_PRIVATE, null)
        val values = ContentValues().apply {
            put("id", item.id.toString())
            put("ticket_id", ticketId.toString())
            put("name", item.name)
            put("category_id", item.category.id.toString())
            put("quantity", item.quantity)
            put("isIntUnit", if (item.isIntUnit) 1 else 0)
            put("price", item.price)
        }
        db.insert("ticket_items", null, values)
        db.close()
        val items = repo.getItemsByTicketId(ticketId)
        assertTrue(items.isNotEmpty())
        assertEquals("TestItem", items.first().name)
        assertEquals(category.name, items.first().category.name)
        assertEquals(category.color, items.first().category.color)
    }

    @Test
    fun testUpdateTicketItem() = runBlocking {
        val category = Category(UUID.randomUUID(), "TestCat", Color.Red)
        categoryRepo.insertCategory(category)
        val ticketId = UUID.randomUUID()
        val item = TicketItem(UUID.randomUUID(), "TestItem", category, 2, true, 10.0)
        val db = context.openOrCreateDatabase("ticketscan.db", Context.MODE_PRIVATE, null)
        val values = ContentValues().apply {
            put("id", item.id.toString())
            put("ticket_id", ticketId.toString())
            put("name", item.name)
            put("category_id", item.category.id.toString())
            put("quantity", item.quantity)
            put("isIntUnit", if (item.isIntUnit) 1 else 0)
            put("price", item.price)
        }
        db.insert("ticket_items", null, values)
        db.close()
        val updated = item.copy(name = "UpdatedItem", quantity = 5)
        assertTrue(repo.updateItem(updated))
        val items = repo.getItemsByTicketId(ticketId)
        assertEquals("UpdatedItem", items.first().name)
        assertEquals(5, items.first().quantity)
    }

    @Test
    fun testDeleteTicketItem() = runBlocking {
        val category = Category(UUID.randomUUID(), "TestCat", Color.Red)
        categoryRepo.insertCategory(category)
        val ticketId = UUID.randomUUID()
        val item = TicketItem(UUID.randomUUID(), "TestItem", category, 2, true, 10.0)
        val db = context.openOrCreateDatabase("ticketscan.db", Context.MODE_PRIVATE, null)
        val values = ContentValues().apply {
            put("id", item.id.toString())
            put("ticket_id", ticketId.toString())
            put("name", item.name)
            put("category_id", item.category.id.toString())
            put("quantity", item.quantity)
            put("isIntUnit", if (item.isIntUnit) 1 else 0)
            put("price", item.price)
        }
        db.insert("ticket_items", null, values)
        db.close()
        assertTrue(repo.deleteItem(item.id))
        val items = repo.getItemsByTicketId(ticketId)
        assertTrue(items.none { it.id == item.id })
    }
}
