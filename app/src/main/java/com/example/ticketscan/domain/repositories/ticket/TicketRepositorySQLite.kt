package com.example.ticketscan.domain.repositories.ticket

import android.content.Context
import android.util.Log
import com.example.ticketscan.data.database.DatabaseHelper
import com.example.ticketscan.domain.model.Store
import com.example.ticketscan.domain.model.Ticket
import com.example.ticketscan.domain.repositories.ticketitem.TicketItemRepository
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

private const val TAG = "TicketRepositorySQLite"

class TicketRepositorySQLite(
    private val context: Context,
    private val ticketItemRepository: TicketItemRepository
) : TicketRepository {
    private val dbHelper = DatabaseHelper.getInstance(context)
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

    override suspend fun getTicketById(id: UUID): Ticket? {
        val db = dbHelper.readableDatabase
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val cursor = db.rawQuery("SELECT t.id, t.date, t.store_id, s.name as store_name, s.cuit as store_cuit, s.location as store_location, t.total FROM tickets t LEFT JOIN stores s ON s.id = t.store_id WHERE t.id = ?", arrayOf(id.toString()))
        val ticket = if (cursor.moveToFirst()) {
            val dateStr = cursor.getString(cursor.getColumnIndexOrThrow("date"))
            val storeId: String? = cursor.getString(cursor.getColumnIndexOrThrow("store_id"))
            val store = if (storeId != null) {
                val storeName = cursor.getString(cursor.getColumnIndexOrThrow("store_name"))
                val storeCuit = cursor.getLong(cursor.getColumnIndexOrThrow("store_cuit"))
                val storeLocation = cursor.getString(cursor.getColumnIndexOrThrow("store_location"))
                Store(UUID.fromString(storeId), storeName, storeCuit, storeLocation)
            } else null
            val total = cursor.getDouble(cursor.getColumnIndexOrThrow("total"))
            val items = ticketItemRepository.getItemsByTicketId(id)
            Ticket(id = id, dateFormat.parse(dateStr) ?: Date(), store = store, items = items, total = total)
        } else null
        cursor.close()
        return ticket
    }

    override suspend fun getAllTickets(): List<Ticket> {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT t.id, t.date, t.store_id, s.name as store_name, s.cuit as store_cuit, s.location as store_location, t.total FROM tickets t LEFT JOIN stores s ON s.id = t.store_id", null)
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val tickets = mutableListOf<Ticket>()
        while (cursor.moveToNext()) {
            val idStr = cursor.getString(cursor.getColumnIndexOrThrow("id"))
            val id = UUID.fromString(idStr)
            val dateStr = cursor.getString(cursor.getColumnIndexOrThrow("date"))
            val date = dateFormat.parse(dateStr) ?: Date()
            val storeId = cursor.getString(cursor.getColumnIndexOrThrow("store_id"))
            val store = if (storeId != null) {
                val storeName = cursor.getString(cursor.getColumnIndexOrThrow("store_name"))
                val storeCuit = cursor.getLong(cursor.getColumnIndexOrThrow("store_cuit"))
                val storeLocation = cursor.getString(cursor.getColumnIndexOrThrow("store_location"))
                Store(UUID.fromString(storeId), storeName, storeCuit, storeLocation)
            } else null
            val total = cursor.getDouble(cursor.getColumnIndexOrThrow("total"))
            val items = ticketItemRepository.getItemsByTicketId(id)
            tickets.add(Ticket(id = id, date = date, store = store, items = items, total = total))
        }
        cursor.close()
        return tickets
    }

    override suspend fun insertTicket(ticket: Ticket): Boolean {
        val db = dbHelper.writableDatabase
        val values = android.content.ContentValues().apply {
            put("id", ticket.id.toString())
            put("date", SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(ticket.date))
            if (ticket.store != null) put("store_id", ticket.store.id.toString())
            put("total", ticket.total)
        }
        try {
            db.beginTransaction()
            val result = db.insert("tickets", null, values)
            if (result == -1L) {
                Log.e(TAG, "Failed to insert ticket: ${ticket.id}")
                return false
            }

            for (item in ticket.items) {
                val ok = ticketItemRepository.insertItem(item, ticket.id, db)
                if (!ok) {
                    Log.e(TAG, "Failed to insert ticket item for ticket: ${ticket.id}")
                    return false
                }
            }

            db.setTransactionSuccessful()
            return true
        } finally {
            try {
                db.endTransaction()
            } catch (_: Exception) {
                // ignorar
            }
        }
    }

    override suspend fun updateTicket(ticket: Ticket): Boolean {
        val db = dbHelper.writableDatabase
        val values = android.content.ContentValues().apply {
            put("date", SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(ticket.date))
            if (ticket.store != null) put("store_id", ticket.store.id.toString())
            put("total", ticket.total)
        }
        val result = db.update("tickets", values, "id = ?", arrayOf(ticket.id.toString()))
        return result > 0
    }

    override suspend fun deleteTicket(id: UUID): Boolean {
        val db = dbHelper.writableDatabase
        try {
            db.beginTransaction()
            db.delete("ticket_items", "ticket_id = ?", arrayOf(id.toString()))
            val result = db.delete("tickets", "id = ?", arrayOf(id.toString()))
            db.setTransactionSuccessful()
            return result > 0
        } finally {
            try {
                db.endTransaction()
            } catch (_: Exception) {
                // ignorar
            }
        }
    }

    override suspend fun getTicketsByFilters(categoryName: String?, minDate: Date?): List<Ticket> {
        val db = dbHelper.readableDatabase
        val tickets = mutableListOf<Ticket>()
        val selectionArgs = mutableListOf<String>()

        var query = "SELECT DISTINCT t.id FROM tickets t"

        if (categoryName != null) {
            query += " JOIN ticket_items ti ON t.id = ti.ticket_id JOIN categories c ON ti.category_id = c.id"
        }

        val whereClauses = mutableListOf<String>()
        if (categoryName != null) {
            whereClauses.add("c.name = ?")
            selectionArgs.add(categoryName)
        }
        if (minDate != null) {
            whereClauses.add("t.date >= ?")
            selectionArgs.add(dateFormat.format(minDate))
        }

        if (whereClauses.isNotEmpty()) {
            query += " WHERE " + whereClauses.joinToString(separator = " AND ")
        }

        query += " ORDER BY t.date DESC"

        val cursor = db.rawQuery(query, selectionArgs.toTypedArray())

        while (cursor.moveToNext()) {
            val ticketId = cursor.getString(cursor.getColumnIndexOrThrow("id"))
            getTicketById(UUID.fromString(ticketId))?.let { tickets.add(it) }
        }
        cursor.close()
        return tickets
    }
}
