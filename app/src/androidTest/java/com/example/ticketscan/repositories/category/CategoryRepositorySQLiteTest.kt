package com.example.ticketscan.repositories.category

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.ticketscan.domain.model.Category
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.UUID
import kotlinx.coroutines.runBlocking
import androidx.compose.ui.graphics.Color
import com.example.ticketscan.domain.repositories.category.CategoryRepositorySQLite
import com.example.ticketscan.domain.repositories.icon.IconRepositorySQLite
import com.example.ticketscan.domain.model.Icon

@RunWith(AndroidJUnit4::class)
class CategoryRepositorySQLiteTest {
    private lateinit var repo: CategoryRepositorySQLite
    private lateinit var context: Context

    @Before
    fun setup() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
        val iconRepository = IconRepositorySQLite(context)
        repo = CategoryRepositorySQLite(context, iconRepository)
        val db = context.openOrCreateDatabase("ticketscan.db", Context.MODE_PRIVATE, null)
        db.execSQL("CREATE TABLE IF NOT EXISTS icons (id TEXT PRIMARY KEY, name TEXT NOT NULL, icon_key TEXT NOT NULL)")
        db.execSQL("CREATE TABLE IF NOT EXISTS categories (id TEXT PRIMARY KEY, name TEXT NOT NULL, color INTEGER NOT NULL, icon_id TEXT NOT NULL, is_active INTEGER NOT NULL DEFAULT 1)")
        db.close()
    }

    @Test
    fun testInsertAndGetCategory() = runBlocking {
        val icon = Icon.default()
        val db = context.openOrCreateDatabase("ticketscan.db", Context.MODE_PRIVATE, null)
        db.execSQL("INSERT OR IGNORE INTO icons (id, name, icon_key) VALUES (?, ?, ?)", arrayOf(icon.id.toString(), icon.name, icon.iconKey))
        db.close()
        val category = Category(UUID.randomUUID(), "TestCat", Color.Red, icon)
        assertTrue(repo.insertCategory(category))
        val result = repo.getCategoryById(category.id)
        assertNotNull(result)
        assertEquals(category.name, result?.name)
        assertEquals(category.color, result?.color)
    }

    @Test
    fun testUpdateCategory() = runBlocking {
        val icon = Icon.default()
        val db = context.openOrCreateDatabase("ticketscan.db", Context.MODE_PRIVATE, null)
        db.execSQL("INSERT OR IGNORE INTO icons (id, name, icon_key) VALUES (?, ?, ?)", arrayOf(icon.id.toString(), icon.name, icon.iconKey))
        db.close()
        val category = Category(UUID.randomUUID(), "TestCat", Color.Red, icon)
        repo.insertCategory(category)
        val updated = category.copy(name = "UpdatedCat", color = Color.Blue)
        assertTrue(repo.updateCategory(updated))
        val result = repo.getCategoryById(category.id)
        assertEquals("UpdatedCat", result?.name)
        assertEquals(Color.Blue, result?.color)
    }

    @Test
    fun testDeleteCategory() = runBlocking {
        val icon = Icon.default()
        val db = context.openOrCreateDatabase("ticketscan.db", Context.MODE_PRIVATE, null)
        db.execSQL("INSERT OR IGNORE INTO icons (id, name, icon_key) VALUES (?, ?, ?)", arrayOf(icon.id.toString(), icon.name, icon.iconKey))
        db.close()
        val category = Category(UUID.randomUUID(), "TestCat", Color.Red, icon)
        repo.insertCategory(category)
        assertTrue(repo.deleteCategory(category.id))
        // deleteCategory does a soft delete (sets isActive=false), so getCategoryById still returns it
        val result = repo.getCategoryById(category.id)
        assertNotNull(result)
        assertFalse(result?.isActive ?: true)
        // But it should not be in the active categories list
        val activeCategories = repo.getActiveCategories()
        assertTrue(activeCategories.none { it.id == category.id })
    }
}
