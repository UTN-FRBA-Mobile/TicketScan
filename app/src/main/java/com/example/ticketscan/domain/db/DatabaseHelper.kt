package com.example.ticketscan.domain.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "ticketscan.db"
        private const val DATABASE_VERSION = 1
        private const val TAG = "DatabaseHelper"
    }

    override fun onCreate(db: SQLiteDatabase) {
        Log.d(TAG, "Creating database tables...")
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS stores (
                id TEXT PRIMARY KEY,
                name TEXT NOT NULL,
                cuit INTEGER NOT NULL,
                location TEXT NOT NULL
            );
        """)
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS categories (
                id TEXT PRIMARY KEY,
                name TEXT NOT NULL,
                color INTEGER NOT NULL
            );
        """)
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS tickets (
                id TEXT PRIMARY KEY,
                date TEXT NOT NULL,
                store_id TEXT NOT NULL,
                total REAL NOT NULL,
                FOREIGN KEY(store_id) REFERENCES stores(id)
            );
        """)
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
        Log.d(TAG, "Database tables created successfully.")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        Log.w(TAG, "Upgrading database from version $oldVersion to $newVersion, which will destroy all old data")
        db.execSQL("DROP TABLE IF EXISTS ticket_items;")
        db.execSQL("DROP TABLE IF EXISTS tickets;")
        db.execSQL("DROP TABLE IF EXISTS categories;")
        db.execSQL("DROP TABLE IF EXISTS stores;")
        onCreate(db)
    }
}
