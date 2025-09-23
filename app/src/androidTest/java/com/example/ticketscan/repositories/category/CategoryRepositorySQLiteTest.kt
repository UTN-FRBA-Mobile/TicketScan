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

@RunWith(AndroidJUnit4::class)
class CategoryRepositorySQLiteTest {
    private lateinit var repo: CategoryRepositorySQLite
    private lateinit var context: Context

    @Before
    fun setup() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
        repo = CategoryRepositorySQLite(context)
        val db = context.openOrCreateDatabase("ticketscan.db", Context.MODE_PRIVATE, null)
        db.execSQL("CREATE TABLE IF NOT EXISTS categories (id TEXT PRIMARY KEY, name TEXT NOT NULL, color INTEGER NOT NULL)")
        db.close()
    }

    @Test
    fun testInsertAndGetCategory() = runBlocking {
        val category = Category(UUID.randomUUID(), "TestCat", Color.Red)
        assertTrue(repo.insertCategory(category))
        val result = repo.getCategoryById(category.id)
        assertNotNull(result)
        assertEquals(category.name, result?.name)
        assertEquals(category.color, result?.color)
    }

    @Test
    fun testUpdateCategory() = runBlocking {
        val category = Category(UUID.randomUUID(), "TestCat", Color.Red)
        repo.insertCategory(category)
        val updated = category.copy(name = "UpdatedCat", color = Color.Blue)
        assertTrue(repo.updateCategory(updated))
        val result = repo.getCategoryById(category.id)
        assertEquals("UpdatedCat", result?.name)
        assertEquals(Color.Blue, result?.color)
    }

    @Test
    fun testDeleteCategory() = runBlocking {
        val category = Category(UUID.randomUUID(), "TestCat", Color.Red)
        repo.insertCategory(category)
        assertTrue(repo.deleteCategory(category.id))
        val result = repo.getCategoryById(category.id)
        assertNull(result)
    }
}
