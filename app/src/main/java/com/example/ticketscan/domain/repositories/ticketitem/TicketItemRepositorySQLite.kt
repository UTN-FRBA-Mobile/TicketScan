package com.example.ticketscan.domain.repositories.ticketitem

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.example.ticketscan.data.database.DatabaseHelper
import com.example.ticketscan.domain.model.TicketItem
import com.example.ticketscan.domain.repositories.category.CategoryRepository
import java.util.UUID

class TicketItemRepositorySQLite(
    private val context: Context,
    private val categoryRepository: CategoryRepository
) : TicketItemRepository {
    private val dbHelper = DatabaseHelper.getInstance(context)

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
        return items
    }

    override suspend fun insertItem(item: TicketItem, ticketId: UUID, db: SQLiteDatabase?): Boolean {
        var ownDb: SQLiteDatabase? = null
        val database = db ?: run {
            ownDb = dbHelper.writableDatabase
            ownDb
        }
        try {
            val values = ContentValues().apply {
                put("id", item.id.toString())
                put("ticket_id", ticketId.toString())
                put("name", item.name)
                put("category_id", item.category.id.toString())
                put("quantity", item.quantity)
                put("isIntUnit", if (item.isIntUnit) 1 else 0)
                put("price", item.price)
            }
            val result = database.insert("ticket_items", null, values)
            return result != -1L
        } finally {
            // Solo cerramos la DB si la abrimos aquí
            ownDb?.close()
        }
    }

    override suspend fun updateItem(item: TicketItem, db: SQLiteDatabase?): Boolean {
        var ownDb: SQLiteDatabase? = null
        val database = db ?: run {
            ownDb = dbHelper.writableDatabase
            ownDb
        }
        try {
            val values = ContentValues().apply {
                put("name", item.name)
                put("category_id", item.category.id.toString())
                put("quantity", item.quantity)
                put("isIntUnit", if (item.isIntUnit) 1 else 0)
                put("price", item.price)
            }
            val result = database.update("ticket_items", values, "id = ?", arrayOf(item.id.toString()))
            return result > 0
        } finally {
            ownDb?.close()
        }
    }

    override suspend fun deleteItem(id: UUID): Boolean {
        val db = dbHelper.writableDatabase
        val result = db.delete("ticket_items", "id = ?", arrayOf(id.toString()))
        return result > 0
    }
}
