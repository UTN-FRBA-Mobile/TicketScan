package com.example.ticketscan.data.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import androidx.compose.ui.graphics.Color
import android.util.Log

class DatabaseHelper private constructor(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "ticketscan.db"
        private const val DATABASE_VERSION = 1
        private const val TAG = "DatabaseHelper"

        @Volatile
        private var INSTANCE: DatabaseHelper? = null

        fun getInstance(context: Context): DatabaseHelper {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: DatabaseHelper(context.applicationContext).also { INSTANCE = it }
            }
        }
    }

    override fun onCreate(db: SQLiteDatabase) {
        Log.d(TAG, "Creating database tables...")
        db.execSQL(CREATE_STORES_TABLE)
        db.execSQL(CREATE_CATEGORIES_TABLE)
        db.execSQL(CREATE_TICKETS_TABLE)
        db.execSQL(CREATE_TICKET_ITEMS_TABLE)
        db.execSQL(CREATE_MONTHLY_EXPENSES_VIEW)
        db.execSQL(CREATE_WEEKLY_EXPENSES_VIEW)

        initializeDefaultData(db)
        Log.d(TAG, "Database tables and views created successfully.")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS ticket_items")
        db.execSQL("DROP TABLE IF EXISTS tickets")
        db.execSQL("DROP TABLE IF EXISTS categories")
        db.execSQL("DROP TABLE IF EXISTS stores")
        db.execSQL("DROP VIEW IF EXISTS vw_monthly_expenses")
        db.execSQL("DROP VIEW IF EXISTS vw_weekly_expenses")
        onCreate(db)
    }

    private val CREATE_STORES_TABLE = """
        CREATE TABLE IF NOT EXISTS stores (
            id TEXT PRIMARY KEY,
            name TEXT NOT NULL,
            cuit INTEGER NOT NULL,
            location TEXT NOT NULL
        );
    """

    private val CREATE_CATEGORIES_TABLE = """
        CREATE TABLE IF NOT EXISTS categories (
            id TEXT PRIMARY KEY,
            name TEXT NOT NULL,
            color INTEGER NOT NULL
        );
    """

    private val CREATE_TICKETS_TABLE = """
        CREATE TABLE IF NOT EXISTS tickets (
            id TEXT PRIMARY KEY,
            date TEXT NOT NULL,
            store_id TEXT NOT NULL,
            total REAL NOT NULL,
            FOREIGN KEY(store_id) REFERENCES stores(id)
        );
    """

    private val CREATE_TICKET_ITEMS_TABLE = """
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
    """

    private val CREATE_MONTHLY_EXPENSES_VIEW = """
        CREATE VIEW IF NOT EXISTS vw_monthly_expenses AS
        SELECT
            c.name AS category_name,
            c.color AS category_color,
            strftime('%Y-%m', t.date) AS period,
            SUM(ti.price * ti.quantity) AS total
        FROM ticket_items ti
        JOIN tickets t ON ti.ticket_id = t.id
        JOIN categories c ON c.id = ti.category_id
        GROUP BY category_name, category_color, period;
    """

    private val CREATE_WEEKLY_EXPENSES_VIEW = """
        CREATE VIEW IF NOT EXISTS vw_weekly_expenses AS
        SELECT
            c.name AS category_name,
            c.color AS category_color,
            strftime('%Y-%W', t.date) AS period,
            SUM(ti.price * ti.quantity) AS total
        FROM ticket_items ti
        JOIN tickets t ON ti.ticket_id = t.id
        JOIN categories c ON c.id = ti.category_id
        GROUP BY category_name, category_color, period;
    """

    private fun initializeDefaultData(db: SQLiteDatabase) {
        // Verificar si ya hay datos
        val cursor = db.rawQuery("SELECT COUNT(*) FROM categories", null)
        cursor.moveToFirst()
        val count = cursor.getInt(0)
        cursor.close()

        if (count == 0) {
            // Insertar categorías por defecto
            val defaultCategories = listOf(
                "('${java.util.UUID.randomUUID()}', 'Alimentación', ${Color.Green})",
                "('${java.util.UUID.randomUUID()}', 'Transporte', ${Color.Magenta})",
                "('${java.util.UUID.randomUUID()}', 'Entretenimiento', ${Color(0xFFFF9800)})",
                "('${java.util.UUID.randomUUID()}', 'Salud', ${Color(0xFFF44336)})",
                "('${java.util.UUID.randomUUID()}', 'Hogar', ${Color(0xFF9C27B0)})",
                "('${java.util.UUID.randomUUID()}', 'Otros', ${Color.Gray})"
            )

            for (category in defaultCategories) {
                db.execSQL("INSERT INTO categories (id, name, color) VALUES $category")
            }
        }
    }
}
