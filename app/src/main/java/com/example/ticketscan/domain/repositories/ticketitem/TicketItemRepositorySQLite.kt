package com.example.ticketscan.domain.repositories.ticketitem

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.ticketscan.domain.model.TicketItem
import java.util.UUID

class TicketItemRepositorySQLite(
    private val context: Context,
    private val categoryRepository: com.example.ticketscan.domain.repositories.category.CategoryRepository
) : TicketItemRepository {
    private val dbHelper = object : SQLiteOpenHelper(context, "ticketscan.db", null, 1) {
        override fun onCreate(db: SQLiteDatabase) {
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
            onCreate(db)
        }
    }

    override suspend fun getItemsByTicketId(ticketId: UUID): List<TicketItem> {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT id, name, category_id, quantity, isIntUnit, price FROM ticket_items WHERE ticket_id = ?", arrayOf(ticketId.toString()))
        val items = mutableListOf<TicketItem>()
        while (cursor.moveToNext()) {
            val idStr = cursor.getString(cursor.getColumnIndexOrThrow("id"))
            val id = UUID.fromString(idStr)
            val name = cursor.getString(cursor.getColumnIndexOrThrow("name"))
            val categoryIdStr = cursor.getString(cursor.getColumnIndexOrThrow("category_id"))
            val category = categoryRepository.getCategoryById(UUID.fromString(categoryIdStr)) ?: com.example.ticketscan.domain.model.Category(UUID.fromString(categoryIdStr), "", androidx.compose.ui.graphics.Color.Gray)
            val quantity = cursor.getInt(cursor.getColumnIndexOrThrow("quantity"))
            val isIntUnit = cursor.getInt(cursor.getColumnIndexOrThrow("isIntUnit")) != 0
            val price = cursor.getDouble(cursor.getColumnIndexOrThrow("price"))
            items.add(TicketItem(id, name, category, quantity, isIntUnit, price))
        }
        cursor.close()
        db.close()
        return items
    }

    override suspend fun insertItem(item: TicketItem): Boolean {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("id", item.id.toString())
            put("name", item.name)
            put("category_id", item.category.id.toString())
            put("quantity", item.quantity)
            put("isIntUnit", if (item.isIntUnit) 1 else 0)
            put("price", item.price)
        }
        val result = db.insert("ticket_items", null, values)
        db.close()
        return result != -1L
    }

    override suspend fun updateItem(item: TicketItem): Boolean {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("name", item.name)
            put("category_id", item.category.id.toString())
            put("quantity", item.quantity)
            put("isIntUnit", if (item.isIntUnit) 1 else 0)
            put("price", item.price)
        }
        val result = db.update("ticket_items", values, "id = ?", arrayOf(item.id.toString()))
        db.close()
        return result > 0
    }

    override suspend fun deleteItem(id: UUID): Boolean {
        val db = dbHelper.writableDatabase
        val result = db.delete("ticket_items", "id = ?", arrayOf(id.toString()))
        db.close()
        return result > 0
    }
}
