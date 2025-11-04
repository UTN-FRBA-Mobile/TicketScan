package com.example.ticketscan.data.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import android.util.Log

class DatabaseHelper private constructor(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "ticketscan.db"
        private const val DATABASE_VERSION = 1
        private const val TAG = "DatabaseHelper"

        @Volatile
        private var instance: DatabaseHelper? = null

        fun getInstance(context: Context): DatabaseHelper {
            return instance ?: synchronized(this) {
                instance ?: DatabaseHelper(context.applicationContext).also { instance = it }
            }
        }
    }

    override fun onCreate(db: SQLiteDatabase) {
        Log.d(TAG, "Creating database tables...")
        db.execSQL(CREATE_ICONS_TABLE)
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
        db.execSQL("DROP TABLE IF EXISTS icons")
        db.execSQL("DROP VIEW IF EXISTS vw_monthly_expenses")
        db.execSQL("DROP VIEW IF EXISTS vw_weekly_expenses")
        onCreate(db)
    }

    private val CREATE_ICONS_TABLE = """
        CREATE TABLE IF NOT EXISTS icons (
            id TEXT PRIMARY KEY,
            name TEXT NOT NULL,
            icon_key TEXT NOT NULL
        );
    """

    private val CREATE_STORES_TABLE = """
        CREATE TABLE IF NOT EXISTS stores (
            id TEXT PRIMARY KEY,
            name TEXT NOT NULL,
            cuit INTEGER,
            location TEXT
        );
    """

    private val CREATE_CATEGORIES_TABLE = """
        CREATE TABLE IF NOT EXISTS categories (
            id TEXT PRIMARY KEY,
            name TEXT NOT NULL,
            color INTEGER NOT NULL,
            icon_id TEXT NOT NULL,
            is_active INTEGER NOT NULL DEFAULT 1,
            FOREIGN KEY(icon_id) REFERENCES icons(id)
        );
    """

    private val CREATE_TICKETS_TABLE = """
        CREATE TABLE IF NOT EXISTS tickets (
            id TEXT PRIMARY KEY,
            date TEXT NOT NULL,
            store_id TEXT,
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
            SUM(ti.price) AS total
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
            SUM(ti.price) AS total
        FROM ticket_items ti
        JOIN tickets t ON ti.ticket_id = t.id
        JOIN categories c ON c.id = ti.category_id
        GROUP BY category_name, category_color, period;
    """

    private fun initializeDefaultData(db: SQLiteDatabase) {
        // Verificar si ya hay datos
        val cursorIcons = db.rawQuery("SELECT COUNT(*) FROM icons", null)
        cursorIcons.moveToFirst()
        val iconCount = cursorIcons.getInt(0)
        cursorIcons.close()

        if (iconCount == 0) {
            // Insertar iconos por defecto
            val defaultIcons = mapOf(
                "Alimentación" to java.util.UUID.randomUUID().toString(),
                "Transporte" to java.util.UUID.randomUUID().toString(),
                "Entretenimiento" to java.util.UUID.randomUUID().toString(),
                "Salud" to java.util.UUID.randomUUID().toString(),
                "Hogar" to java.util.UUID.randomUUID().toString(),
                "Compras" to java.util.UUID.randomUUID().toString(),
                "Otros" to java.util.UUID.randomUUID().toString()
            )

            for ((name, id) in defaultIcons) {
                db.execSQL("INSERT INTO icons (id, name, icon_key) VALUES ('$id', '$name', '$name')")
            }

            // Verificar si ya hay categorías
            val cursorCategories = db.rawQuery("SELECT COUNT(*) FROM categories", null)
            cursorCategories.moveToFirst()
            val categoryCount = cursorCategories.getInt(0)
            cursorCategories.close()

            if (categoryCount == 0) {
                // Insertar categorías por defecto con sus iconos correspondientes
                val defaultCategories = listOf(
                    Triple("Alimentación", Color(red = 0, green = 150, blue = 136, alpha = 255), defaultIcons["Alimentación"]!!),
                    Triple("Transporte", Color(red = 33, green = 150, blue = 243, alpha = 255), defaultIcons["Transporte"]!!),
                    Triple("Entretenimiento", Color(0xFFFF9800), defaultIcons["Entretenimiento"]!!),
                    Triple("Salud", Color(0xFFF44336), defaultIcons["Salud"]!!),
                    Triple("Hogar", Color(0xFF9C27B0), defaultIcons["Hogar"]!!),
                    Triple("Otros", Color.Gray, defaultIcons["Otros"]!!)
                )

                for ((name, color, iconId) in defaultCategories) {
                    val categoryId = java.util.UUID.randomUUID().toString()
                    db.execSQL(
                        "INSERT INTO categories (id, name, color, icon_id, is_active) VALUES ('$categoryId', '$name', ${color.toArgb()}, '$iconId', 1)"
                    )
                }
            }
        }
    }
}
