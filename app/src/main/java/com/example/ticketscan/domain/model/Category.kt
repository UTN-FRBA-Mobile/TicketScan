package com.example.ticketscan.domain.model

import java.util.UUID

sealed class Category(
    val id: UUID,
    val name: String
) {
    object Food : Category(UUID.randomUUID(), "Alimentos")
    object Cleaning : Category(UUID.randomUUID(), "Limpieza")
    object Clothing : Category(UUID.randomUUID(), "Ropa")
    object Electronics : Category(UUID.randomUUID(), "Electrónica")
    object Other : Category(UUID.randomUUID(), "Otros")

    companion object {
        fun fromName(name: String): Category {
            return when (name) {
                Food.name -> Food
                Cleaning.name -> Cleaning
                Clothing.name -> Clothing
                Electronics.name -> Electronics
                else -> Other
            }
        }

        fun getAllCategories(): List<Category> {
            return listOf(Food, Cleaning, Clothing, Electronics)
        }
    }
}
