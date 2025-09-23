package com.example.ticketscan.domain.repositories.ticket

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
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
        }
        override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
            db.execSQL("DROP TABLE IF EXISTS tickets;")
            onCreate(db)
        }
    }

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
        db.close()
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
        db.close()
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
        db.close()
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
        val result = db.insert("tickets", null, values)
        db.close()
        return result != -1L
    }

    override suspend fun updateTicket(ticket: Ticket): Boolean {
        val db = dbHelper.writableDatabase
        val values = android.content.ContentValues().apply {
            put("date", SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(ticket.date))
            put("store_id", ticket.store.id.toString())
            put("total", ticket.total)
        }
        val result = db.update("tickets", values, "id = ?", arrayOf(ticket.id.toString()))
        db.close()
        return result > 0
    }

    override suspend fun deleteTicket(id: UUID): Boolean {
        val db = dbHelper.writableDatabase
        val result = db.delete("tickets", "id = ?", arrayOf(id.toString()))
        db.close()
        return result > 0
    }
}
