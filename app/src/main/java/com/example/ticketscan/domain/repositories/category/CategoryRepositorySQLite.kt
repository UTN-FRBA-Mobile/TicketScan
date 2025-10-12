package com.example.ticketscan.domain.repositories.category

import android.content.ContentValues
import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.example.ticketscan.domain.db.DatabaseHelper
import com.example.ticketscan.domain.model.Category
import java.util.UUID

class CategoryRepositorySQLite(context: Context) : CategoryRepository {
    private val dbHelper = DatabaseHelper(context)

    override suspend fun getAllCategories(): List<Category> {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT id, name, color FROM categories", null)
        val categories = mutableListOf<Category>()
        while (cursor.moveToNext()) {
            val id = UUID.fromString(cursor.getString(cursor.getColumnIndexOrThrow("id")))
            val name = cursor.getString(cursor.getColumnIndexOrThrow("name"))
            val colorInt = cursor.getInt(cursor.getColumnIndexOrThrow("color"))
            val color = Color(colorInt)
            categories.add(Category(id, name, color))
        }
        cursor.close()
        return categories
    }

    override suspend fun getCategoryById(id: UUID): Category? {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT id, name, color FROM categories WHERE id = ?", arrayOf(id.toString()))
        val category = if (cursor.moveToFirst()) {
            val name = cursor.getString(cursor.getColumnIndexOrThrow("name"))
            val colorInt = cursor.getInt(cursor.getColumnIndexOrThrow("color"))
            val color = Color(colorInt)
            Category(id, name, color)
        } else null
        cursor.close()
        return category
    }

    override suspend fun insertCategory(category: Category): Boolean {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("id", category.id.toString())
            put("name", category.name)
            put("color", category.color.toArgb())
        }
        val result = db.insert("categories", null, values)
        return result != -1L
    }

    override suspend fun updateCategory(category: Category): Boolean {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("name", category.name)
            put("color", category.color.toArgb())
        }
        val result = db.update("categories", values, "id = ?", arrayOf(category.id.toString()))
        return result > 0
    }

    override suspend fun deleteCategory(id: UUID): Boolean {
        val db = dbHelper.writableDatabase
        val result = db.delete("categories", "id = ?", arrayOf(id.toString()))
        return result > 0
    }
}
