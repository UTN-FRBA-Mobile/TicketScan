package com.example.ticketscan.domain.repositories.ticket

import android.content.Context
import com.example.ticketscan.domain.db.DatabaseHelper
import com.example.ticketscan.domain.model.Store
import com.example.ticketscan.domain.model.Ticket
import com.example.ticketscan.domain.repositories.store.StoreRepository
import com.example.ticketscan.domain.repositories.ticketitem.TicketItemRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class TicketRepositorySQLite(
    private val context: Context,
    private val storeRepository: StoreRepository,
    private val ticketItemRepository: TicketItemRepository
) : TicketRepository {
    private val dbHelper = DatabaseHelper(context)

    override suspend fun processTicket(): Flow<Ticket> = flow {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT id, date, store_id, total FROM tickets", null)
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        while (cursor.moveToNext()) {
            val idStr = cursor.getString(cursor.getColumnIndexOrThrow("id"))
            val id = UUID.fromString(idStr)
            val dateStr = cursor.getString(cursor.getColumnIndexOrThrow("date"))
            val date = dateFormat.parse(dateStr) ?: Date()
            val storeIdStr = cursor.getString(cursor.getColumnIndexOrThrow("store_id"))
            val storeId = UUID.fromString(storeIdStr)
            val total = cursor.getDouble(cursor.getColumnIndexOrThrow("total"))
            val store = storeRepository.getStoreById(storeId)
            val items = ticketItemRepository.getItemsByTicketId(id)
            emit(Ticket(id = id, date = date, store = store ?: Store(storeId, "", 0L, ""), items = items, total = total))
        }
        cursor.close()
    }

    override suspend fun getTicketById(id: UUID): Ticket? {
        val db = dbHelper.readableDatabase
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val cursor = db.rawQuery("SELECT id, date, store_id, total FROM tickets WHERE id = ?", arrayOf(id.toString()))
        val ticket = if (cursor.moveToFirst()) {
            val dateStr = cursor.getString(cursor.getColumnIndexOrThrow("date"))
            val date = dateFormat.parse(dateStr) ?: Date()
            val storeIdStr = cursor.getString(cursor.getColumnIndexOrThrow("store_id"))
            val storeId = UUID.fromString(storeIdStr)
            val total = cursor.getDouble(cursor.getColumnIndexOrThrow("total"))
            val store = storeRepository.getStoreById(storeId)
            val items = ticketItemRepository.getItemsByTicketId(id)
            Ticket(id = id, date = date, store = store ?: Store(storeId, "", 0L, ""), items = items, total = total)
        } else null
        cursor.close()
        return ticket
    }

    override suspend fun getAllTickets(): List<Ticket> {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT id, date, store_id, total FROM tickets", null)
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val tickets = mutableListOf<Ticket>()
        while (cursor.moveToNext()) {
            val idStr = cursor.getString(cursor.getColumnIndexOrThrow("id"))
            val id = UUID.fromString(idStr)
            val dateStr = cursor.getString(cursor.getColumnIndexOrThrow("date"))
            val date = dateFormat.parse(dateStr) ?: Date()
            val storeIdStr = cursor.getString(cursor.getColumnIndexOrThrow("store_id"))
            val storeId = UUID.fromString(storeIdStr)
            val total = cursor.getDouble(cursor.getColumnIndexOrThrow("total"))
            val store = storeRepository.getStoreById(storeId)
            val items = ticketItemRepository.getItemsByTicketId(id)
            tickets.add(Ticket(id = id, date = date, store = store ?: Store(storeId, "", 0L, ""), items = items, total = total))
        }
        cursor.close()
        return tickets
    }

    override suspend fun insertTicket(ticket: Ticket): Boolean {
        val db = dbHelper.writableDatabase
        val values = android.content.ContentValues().apply {
            put("id", ticket.id.toString())
            put("date", SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(ticket.date))
            put("store_id", ticket.store.id.toString())
            put("total", ticket.total)
        }
        try {
            db.beginTransaction()
            val result = db.insert("tickets", null, values)
            if (result == -1L) {
                return false
            }

            for (item in ticket.items) {
                val ok = ticketItemRepository.insertItem(item, ticket.id, db)
                if (!ok) {
                    return false
                }
            }

            db.setTransactionSuccessful()
            return true
        } finally {
            try {
                db.endTransaction()
            } catch (_: Exception) {
            }
        }
    }

    override suspend fun updateTicket(ticket: Ticket): Boolean {
        val db = dbHelper.writableDatabase
        val values = android.content.ContentValues().apply {
            put("date", SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(ticket.date))
            put("store_id", ticket.store.id.toString())
            put("total", ticket.total)
        }
        val result = db.update("tickets", values, "id = ?", arrayOf(ticket.id.toString()))
        return result > 0
    }

    override suspend fun deleteTicket(id: UUID): Boolean {
        val db = dbHelper.writableDatabase
        val result = db.delete("tickets", "id = ?", arrayOf(id.toString()))
        return result > 0
    }

    override suspend fun getTicketsByCategory(categoryName: String): List<Ticket> {
        val allTickets = getAllTickets()
        return allTickets.filter { ticket ->
            ticket.items.any { item -> item.category.name == categoryName }
        }
    }
}
