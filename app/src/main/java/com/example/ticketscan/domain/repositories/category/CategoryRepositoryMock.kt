package com.example.ticketscan.domain.repositories.category

import androidx.compose.ui.graphics.Color
import com.example.ticketscan.domain.model.Category
import java.util.UUID

class CategoryRepositoryMock : CategoryRepository {

    private val categories = listOf(
        Category(UUID.randomUUID(), "Alimentación", Color(0xFFEF5350)),
        Category(UUID.randomUUID(), "Transporte", Color(0xFFAB47BC)),
        Category(UUID.randomUUID(), "Salud", Color(0xFF26A69A)),
        Category(UUID.randomUUID(), "Entretenimiento", Color(0xFF42A5F5)),
        Category(UUID.randomUUID(), "Otros", Color(0xFF66BB6A))
    )

    override suspend fun getAllCategories(): List<Category> {
        return categories
    }

    override suspend fun getCategoryById(id: UUID): Category? {
        return categories.find { it.id == id }
    }

    override suspend fun insertCategory(category: Category): Boolean {
        // No-op for mock
        return true
    }

    override suspend fun updateCategory(category: Category): Boolean {
        // No-op for mock
        return true
    }

    override suspend fun deleteCategory(id: UUID): Boolean {
        // No-op for mock
        return true
    }
}
