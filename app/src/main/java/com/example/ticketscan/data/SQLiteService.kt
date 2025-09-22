package com.example.ticketscan.data

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.ticketscan.domain.AnalizedItem

class SQLiteService(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        private const val DATABASE_NAME = "ticketscan.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_ANALIZED_ITEM = "analized_item"
        private const val COLUMN_ID = "id"
        private const val COLUMN_NAME = "name"
        private const val COLUMN_PRICE = "price"
        private const val COLUMN_CATEGORY = "category"
        private const val TABLE_TICKET = "ticket"
        private const val COLUMN_TICKET_ID = "id"
        private const val COLUMN_CREATION_DATE = "creationDate"
    }

    init {
        writableDatabase // Fuerza la creación de la base si no existe
        precargarDatosSiEsNecesario()
    }

    private fun precargarDatosSiEsNecesario() {
        val cursor = readableDatabase.rawQuery("SELECT COUNT(*) FROM ticket", null)
        var count = 0
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0)
        }
        cursor.close()
        if (count == 0) {
            val now = System.currentTimeMillis()
            writableDatabase.execSQL("INSERT INTO ticket (creationDate) VALUES ($now)")
            writableDatabase.execSQL("INSERT INTO ticket (creationDate) VALUES (${now - 86400000})")
            writableDatabase.execSQL("INSERT INTO analized_item (name, price, category, ticketId) VALUES ('Pan', 100.0, 'Alimentos', 1)")
            writableDatabase.execSQL("INSERT INTO analized_item (name, price, category, ticketId) VALUES ('Leche', 200.0, 'Lácteos', 1)")
            writableDatabase.execSQL("INSERT INTO analized_item (name, price, category, ticketId) VALUES ('Queso', 300.0, 'Lácteos', 2)")
            writableDatabase.execSQL("INSERT INTO analized_item (name, price, category, ticketId) VALUES ('Agua', 50.0, 'Bebidas', 2)")
        }
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTicketTable = """
            CREATE TABLE ticket (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                creationDate INTEGER NOT NULL
            )
        """
        db.execSQL(createTicketTable)
        val createAnalizedItemTable = """
            CREATE TABLE analized_item (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL,
                price REAL NOT NULL,
                category TEXT NOT NULL,
                ticketId INTEGER NOT NULL,
                FOREIGN KEY(ticketId) REFERENCES ticket(id)
            )
        """
        db.execSQL(createAnalizedItemTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_ANALIZED_ITEM")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_TICKET")
        onCreate(db)
    }

    fun insertAnalizedItem(item: AnalizedItem) {
        val values = ContentValues().apply {
            put(COLUMN_NAME, item.name)
            put(COLUMN_PRICE, item.price)
            put(COLUMN_CATEGORY, item.category)
            put(COLUMN_TICKET_ID, item.ticketId)
        }
        writableDatabase.insert(TABLE_ANALIZED_ITEM, null, values)
    }

    fun getAllAnalizedItems(): List<AnalizedItem> {
        val items = mutableListOf<AnalizedItem>()
        val cursor: Cursor = readableDatabase.query(
            TABLE_ANALIZED_ITEM,
            arrayOf(COLUMN_ID, COLUMN_NAME, COLUMN_PRICE, COLUMN_CATEGORY, COLUMN_TICKET_ID),
            null, null, null, null, null
        )
        with(cursor) {
            while (moveToNext()) {
                val id = getLong(getColumnIndexOrThrow(COLUMN_ID))
                val name = getString(getColumnIndexOrThrow(COLUMN_NAME))
                val price = getDouble(getColumnIndexOrThrow(COLUMN_PRICE))
                val category = getString(getColumnIndexOrThrow(COLUMN_CATEGORY))
                val ticketId = getLong(getColumnIndexOrThrow(COLUMN_TICKET_ID))
                items.add(AnalizedItem(id, name, price, category, ticketId))
            }
            close()
        }
        return items
    }

    fun deleteAllAnalizedItems() {
        writableDatabase.delete(TABLE_ANALIZED_ITEM, null, null)
    }

    fun updateAnalizedItem(item: AnalizedItem): Int {
        val values = ContentValues().apply {
            put(COLUMN_NAME, item.name)
            put(COLUMN_PRICE, item.price)
            put(COLUMN_CATEGORY, item.category)
            put(COLUMN_TICKET_ID, item.ticketId)
        }
        return writableDatabase.update(
            TABLE_ANALIZED_ITEM,
            values,
            "$COLUMN_ID = ?",
            arrayOf(item.id.toString())
        )
    }

    fun getAnalizedItemById(id: Long): AnalizedItem? {
        val cursor: Cursor = readableDatabase.query(
            TABLE_ANALIZED_ITEM,
            arrayOf(COLUMN_ID, COLUMN_NAME, COLUMN_PRICE, COLUMN_CATEGORY, COLUMN_TICKET_ID),
            "$COLUMN_ID = ?",
            arrayOf(id.toString()),
            null, null, null
        )
        var item: AnalizedItem? = null
        if (cursor.moveToFirst()) {
            val itemId = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID))
            val name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME))
            val price = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_PRICE))
            val category = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY))
            val ticketId = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_TICKET_ID))
            item = AnalizedItem(itemId, name, price, category, ticketId)
        }
        cursor.close()
        return item
    }

    fun getAnalizedItemsByTicketId(ticketId: Long): List<AnalizedItem> {
        val items = mutableListOf<AnalizedItem>()
        val cursor: Cursor = readableDatabase.query(
            TABLE_ANALIZED_ITEM,
            arrayOf(COLUMN_ID, COLUMN_NAME, COLUMN_PRICE, COLUMN_CATEGORY, COLUMN_TICKET_ID),
            "$COLUMN_TICKET_ID = ?",
            arrayOf(ticketId.toString()),
            null, null, null
        )
        with(cursor) {
            while (moveToNext()) {
                val id = getLong(getColumnIndexOrThrow(COLUMN_ID))
                val name = getString(getColumnIndexOrThrow(COLUMN_NAME))
                val price = getDouble(getColumnIndexOrThrow(COLUMN_PRICE))
                val category = getString(getColumnIndexOrThrow(COLUMN_CATEGORY))
                val tId = getLong(getColumnIndexOrThrow(COLUMN_TICKET_ID))
                items.add(AnalizedItem(id, name, price, category, tId))
            }
            close()
        }
        return items
    }

    fun deleteAnalizedItemById(id: Long): Int {
        return writableDatabase.delete(
            TABLE_ANALIZED_ITEM,
            "$COLUMN_ID = ?",
            arrayOf(id.toString())
        )
    }

    fun deleteAnalizedItemsByTicketId(ticketId: Long): Int {
        return writableDatabase.delete(
            TABLE_ANALIZED_ITEM,
            "$COLUMN_TICKET_ID = ?",
            arrayOf(ticketId.toString())
        )
    }

    fun insertTicket(creationDate: Long): Long {
        val values = ContentValues().apply {
            put(COLUMN_CREATION_DATE, creationDate)
        }
        return writableDatabase.insert(TABLE_TICKET, null, values)
    }

    fun getTicketWithItems(ticketId: Long): com.example.ticketscan.domain.Ticket? {
        val cursor = readableDatabase.query(
            TABLE_TICKET,
            arrayOf(COLUMN_TICKET_ID, COLUMN_CREATION_DATE),
            "$COLUMN_TICKET_ID = ?",
            arrayOf(ticketId.toString()),
            null, null, null
        )
        var ticket: com.example.ticketscan.domain.Ticket? = null
        if (cursor.moveToFirst()) {
            val id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_TICKET_ID))
            val creationDate = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_CREATION_DATE))
            val items = getAnalizedItemsByTicketId(id)
            ticket = com.example.ticketscan.domain.Ticket(id, creationDate, items)
        }
        cursor.close()
        return ticket
    }
}
