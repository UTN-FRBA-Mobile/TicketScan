package com.example.ticketscan.domain.repositories.store

import android.content.ContentValues
import android.content.Context
import com.example.ticketscan.data.database.DatabaseHelper
import com.example.ticketscan.domain.model.Store
import java.util.UUID

class StoreRepositorySQLite(context: Context) : StoreRepository {
    private val dbHelper = DatabaseHelper.getInstance(context)

    override suspend fun getAllStores(): List<Store> {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT id, name, cuit, location FROM stores", null)
        val stores = mutableListOf<Store>()
        while (cursor.moveToNext()) {
            val id = UUID.fromString(cursor.getString(cursor.getColumnIndexOrThrow("id")))
            val name = cursor.getString(cursor.getColumnIndexOrThrow("name"))
            val cuitIndex = cursor.getColumnIndexOrThrow("cuit")
            val cuit = if (cursor.isNull(cuitIndex)) null else cursor.getLong(cuitIndex)
            val locationIndex = cursor.getColumnIndexOrThrow("location")
            val location = if (cursor.isNull(locationIndex)) null else cursor.getString(locationIndex)
            stores.add(Store(id, name, cuit, location))
        }
        cursor.close()
        return stores
    }

    override suspend fun getStoreById(id: UUID): Store? {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT id, name, cuit, location FROM stores WHERE id = ?", arrayOf(id.toString()))
        val store = if (cursor.moveToFirst()) {
            val name = cursor.getString(cursor.getColumnIndexOrThrow("name"))
            val cuitIndex = cursor.getColumnIndexOrThrow("cuit")
            val cuit = if (cursor.isNull(cuitIndex)) null else cursor.getLong(cuitIndex)
            val locationIndex = cursor.getColumnIndexOrThrow("location")
            val location = if (cursor.isNull(locationIndex)) null else cursor.getString(locationIndex)
            Store(id, name, cuit, location)
        } else null
        cursor.close()
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
        return result > 0
    }

    override suspend fun deleteStore(id: UUID): Boolean {
        val db = dbHelper.writableDatabase
        val result = db.delete("stores", "id = ?", arrayOf(id.toString()))
        return result > 0
    }

    override suspend fun searchStoresByName(query: String, limit: Int): List<Store> {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(
            "SELECT id, name, cuit, location FROM stores WHERE name LIKE ? ORDER BY name LIMIT ?",
            arrayOf("%$query%", limit.toString())
        )
        val stores = mutableListOf<Store>()
        while (cursor.moveToNext()) {
            val id = UUID.fromString(cursor.getString(cursor.getColumnIndexOrThrow("id")))
            val name = cursor.getString(cursor.getColumnIndexOrThrow("name"))
            val cuitIndex = cursor.getColumnIndexOrThrow("cuit")
            val cuit = if (cursor.isNull(cuitIndex)) null else cursor.getLong(cuitIndex)
            val locationIndex = cursor.getColumnIndexOrThrow("location")
            val location = if (cursor.isNull(locationIndex)) null else cursor.getString(locationIndex)
            stores.add(Store(id, name, cuit, location))
        }
        cursor.close()
        return stores
    }
}
