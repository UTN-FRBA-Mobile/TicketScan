package com.example.ticketscan.data.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb

class DatabaseHelper private constructor(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "ticketscan.db"
        private const val DATABASE_VERSION = 1

        @Volatile
        private var instance: DatabaseHelper? = null

        fun getInstance(context: Context): DatabaseHelper {
            return instance ?: synchronized(this) {
                instance ?: DatabaseHelper(context.applicationContext).also { instance = it }
            }
        }
    }

    override fun onCreate(db: SQLiteDatabase) {
        // Crear tabla de tiendas
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS stores (
                id TEXT PRIMARY KEY,
                name TEXT NOT NULL,
                cuit INTEGER NOT NULL,
                location TEXT NOT NULL
            );
        """)

        // Crear tabla de categorías
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS categories (
                id TEXT PRIMARY KEY,
                name TEXT NOT NULL,
                color INTEGER NOT NULL
            );
        """)

        // Crear tabla de tickets
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS tickets (
                id TEXT PRIMARY KEY,
                date TEXT NOT NULL,
                store_id TEXT,
                total REAL NOT NULL,
                FOREIGN KEY(store_id) REFERENCES stores(id)
            );
        """)

        // Crear tabla de items de tickets
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

        // Inicializar datos por defecto
        initializeDefaultData(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Aquí puedes manejar migraciones de base de datos
        // Por ahora, simplemente recreamos las tablas
        db.execSQL("DROP TABLE IF EXISTS ticket_items")
        db.execSQL("DROP TABLE IF EXISTS tickets")
        db.execSQL("DROP TABLE IF EXISTS categories")
        db.execSQL("DROP TABLE IF EXISTS stores")
        onCreate(db)
    }

    override fun onConfigure(db: SQLiteDatabase) {
        super.onConfigure(db)
        // Habilitar foreign keys
        db.setForeignKeyConstraintsEnabled(true)
    }

    private fun initializeDefaultData(db: SQLiteDatabase) {
        // Aquí puedes insertar categorías por defecto o datos iniciales
        // Por ejemplo, algunas categorías comunes:

        // Verificar si ya hay datos
        val cursor = db.rawQuery("SELECT COUNT(*) FROM categories", null)
        cursor.moveToFirst()
        val count = cursor.getInt(0)
        cursor.close()

        if (count == 0) {
            // Insertar categorías por defecto
            val defaultCategories = listOf(
                "('${java.util.UUID.randomUUID()}', 'Alimentación', ${Color.Green.toArgb()})",
                "('${java.util.UUID.randomUUID()}', 'Transporte', ${Color.Magenta.toArgb()})",
                "('${java.util.UUID.randomUUID()}', 'Entretenimiento', ${Color(0xFFFF9800).toArgb()})",
                "('${java.util.UUID.randomUUID()}', 'Salud', ${Color(0xFFF44336).toArgb()})",
                "('${java.util.UUID.randomUUID()}', 'Hogar', ${Color(0xFF9C27B0).toArgb()})",
                "('${java.util.UUID.randomUUID()}', 'Otros', ${Color.Gray.toArgb()})"
            )

            for (category in defaultCategories) {
                db.execSQL("INSERT INTO categories (id, name, color) VALUES $category")
            }
        }
    }
}
