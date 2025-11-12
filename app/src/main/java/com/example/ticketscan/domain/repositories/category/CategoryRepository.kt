package com.example.ticketscan.domain.repositories.category

import com.example.ticketscan.domain.model.Category
import java.util.UUID

interface CategoryRepository {
    suspend fun getAllCategories(): List<Category>
    suspend fun getActiveCategories(): List<Category>
    suspend fun getCategoryById(id: UUID): Category?
    suspend fun insertCategory(category: Category): Boolean
    suspend fun updateCategory(category: Category): Boolean
    suspend fun deleteCategory(id: UUID): Boolean
    suspend fun toggleCategoryActive(id: UUID, isActive: Boolean): Boolean
}
