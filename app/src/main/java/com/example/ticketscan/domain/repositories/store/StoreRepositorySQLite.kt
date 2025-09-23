package com.example.ticketscan.domain.repositories.store

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.ticketscan.domain.model.Store
import java.util.UUID

class StoreRepositorySQLite(context: Context) : StoreRepository {
    private val dbHelper = object : SQLiteOpenHelper(context, "ticketscan.db", null, 1) {
        override fun onCreate(db: SQLiteDatabase) {
            db.execSQL("""
                CREATE TABLE IF NOT EXISTS stores (
                    id TEXT PRIMARY KEY,
                    name TEXT NOT NULL,
                    cuit INTEGER NOT NULL,
                    location TEXT NOT NULL
                );
            """)
        }

        override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
            db.execSQL("DROP TABLE IF EXISTS stores;")
            onCreate(db)
        }
    }

    override suspend fun getAllStores(): List<Store> {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT id, name, cuit, location FROM stores", null)
        val stores = mutableListOf<Store>()
        while (cursor.moveToNext()) {
            val idStr = cursor.getString(cursor.getColumnIndexOrThrow("id"))
            val id = UUID.fromString(idStr)
            val name = cursor.getString(cursor.getColumnIndexOrThrow("name"))
            val cuit = cursor.getLong(cursor.getColumnIndexOrThrow("cuit"))
            val location = cursor.getString(cursor.getColumnIndexOrThrow("location"))
            stores.add(Store(id, name, cuit, location))
        }
        cursor.close()
        db.close()
        return stores
    }

    override suspend fun getStoreById(id: UUID): Store? {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT id, name, cuit, location FROM stores WHERE id = ?", arrayOf(id.toString()))
        val store = if (cursor.moveToFirst()) {
            val name = cursor.getString(cursor.getColumnIndexOrThrow("name"))
            val cuit = cursor.getLong(cursor.getColumnIndexOrThrow("cuit"))
            val location = cursor.getString(cursor.getColumnIndexOrThrow("location"))
            Store(id, name, cuit, location)
        } else null
        cursor.close()
        db.close()
        return store
    }

    override suspend fun insertStore(store: Store): Boolean {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("id", store.id.toString())
            put("name", store.name)
            put("cuit", store.cuit)
            put("location", store.location)
        }
        val result = db.insert("stores", null, values)
        db.close()
        return result != -1L
    }

    override suspend fun updateStore(store: Store): Boolean {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("name", store.name)
            put("cuit", store.cuit)
            put("location", store.location)
        }
        val result = db.update("stores", values, "id = ?", arrayOf(store.id.toString()))
        db.close()
        return result > 0
    }

    override suspend fun deleteStore(id: UUID): Boolean {
        val db = dbHelper.writableDatabase
        val result = db.delete("stores", "id = ?", arrayOf(id.toString()))
        db.close()
        return result > 0
    }
}
