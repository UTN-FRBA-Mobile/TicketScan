package com.example.ticketscan.domain.repositories.icon

import android.content.Context
import com.example.ticketscan.data.database.DatabaseHelper
import com.example.ticketscan.domain.model.Icon
import java.util.UUID

class IconRepositorySQLite(context: Context) : IconRepository {
    private val dbHelper = DatabaseHelper.getInstance(context)

    override suspend fun getAllIcons(): List<Icon> {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT id, name, icon_key FROM icons", null)
        val icons = mutableListOf<Icon>()
        while (cursor.moveToNext()) {
            val id = UUID.fromString(cursor.getString(cursor.getColumnIndexOrThrow("id")))
            val name = cursor.getString(cursor.getColumnIndexOrThrow("name"))
            val iconKey = cursor.getString(cursor.getColumnIndexOrThrow("icon_key"))
            icons.add(Icon(id, name, iconKey))
        }
        cursor.close()
        return icons
    }

    override suspend fun getIconById(id: UUID): Icon? {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT id, name, icon_key FROM icons WHERE id = ?", arrayOf(id.toString()))
        val icon = if (cursor.moveToFirst()) {
            val name = cursor.getString(cursor.getColumnIndexOrThrow("name"))
            val iconKey = cursor.getString(cursor.getColumnIndexOrThrow("icon_key"))
            Icon(id, name, iconKey)
        } else null
        cursor.close()
        return icon
    }
}

