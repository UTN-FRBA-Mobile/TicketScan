package com.example.ticketscan.domain.repositories.category

import android.content.ContentValues
import android.content.Context
import com.example.ticketscan.data.database.DatabaseHelper
import com.example.ticketscan.domain.model.Category
import java.util.UUID
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.example.ticketscan.domain.model.Icon
import com.example.ticketscan.domain.repositories.icon.IconRepository

class CategoryRepositorySQLite(
    context: Context,
    private val iconRepository: IconRepository
) : CategoryRepository {
    private val dbHelper = DatabaseHelper.getInstance(context)

    override suspend fun getAllCategories(): List<Category> {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT id, name, color, icon_id, is_active FROM categories", null)
        val categories = mutableListOf<Category>()
        while (cursor.moveToNext()) {
            val id = UUID.fromString(cursor.getString(cursor.getColumnIndexOrThrow("id")))
            val name = cursor.getString(cursor.getColumnIndexOrThrow("name"))
            val colorInt = cursor.getInt(cursor.getColumnIndexOrThrow("color"))
            val color = Color(colorInt)
            val iconId = UUID.fromString(cursor.getString(cursor.getColumnIndexOrThrow("icon_id")))
            val isActive = cursor.getInt(cursor.getColumnIndexOrThrow("is_active")) == 1
            val icon = iconRepository.getIconById(iconId) ?: Icon.default()
            categories.add(Category(id, name, color, icon, isActive))
        }
        cursor.close()
        return categories
    }

    override suspend fun getActiveCategories(): List<Category> {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT id, name, color, icon_id, is_active FROM categories WHERE is_active = 1", null)
        val categories = mutableListOf<Category>()
        while (cursor.moveToNext()) {
            val id = UUID.fromString(cursor.getString(cursor.getColumnIndexOrThrow("id")))
            val name = cursor.getString(cursor.getColumnIndexOrThrow("name"))
            val colorInt = cursor.getInt(cursor.getColumnIndexOrThrow("color"))
            val color = Color(colorInt)
            val iconId = UUID.fromString(cursor.getString(cursor.getColumnIndexOrThrow("icon_id")))
            val isActive = cursor.getInt(cursor.getColumnIndexOrThrow("is_active")) == 1
            val icon = iconRepository.getIconById(iconId) ?: Icon.default()
            categories.add(Category(id, name, color, icon, isActive))
        }
        cursor.close()
        return categories
    }

    override suspend fun getCategoryById(id: UUID): Category? {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT id, name, color, icon_id, is_active FROM categories WHERE id = ?", arrayOf(id.toString()))
        val category = if (cursor.moveToFirst()) {
            val name = cursor.getString(cursor.getColumnIndexOrThrow("name"))
            val colorInt = cursor.getInt(cursor.getColumnIndexOrThrow("color"))
            val color = Color(colorInt)
            val iconId = UUID.fromString(cursor.getString(cursor.getColumnIndexOrThrow("icon_id")))
            val isActive = cursor.getInt(cursor.getColumnIndexOrThrow("is_active")) == 1
            val icon = iconRepository.getIconById(iconId) ?: Icon.default()
            Category(id, name, color, icon, isActive)
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
            put("icon_id", category.icon.id.toString())
            put("is_active", if (category.isActive) 1 else 0)
        }
        val result = db.insert("categories", null, values)
        return result != -1L
    }

    override suspend fun updateCategory(category: Category): Boolean {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("name", category.name)
            put("color", category.color.toArgb())
            put("icon_id", category.icon.id.toString())
            put("is_active", if (category.isActive) 1 else 0)
        }
        val result = db.update("categories", values, "id = ?", arrayOf(category.id.toString()))
        return result > 0
    }

    override suspend fun deleteCategory(id: UUID): Boolean {
        return toggleCategoryActive(id, false)
    }

    override suspend fun toggleCategoryActive(id: UUID, isActive: Boolean): Boolean {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("is_active", if (isActive) 1 else 0)
        }
        val result = db.update("categories", values, "id = ?", arrayOf(id.toString()))
        return result > 0
    }
}
