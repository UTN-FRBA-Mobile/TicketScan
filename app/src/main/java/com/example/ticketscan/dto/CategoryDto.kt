package com.example.ticketscan.dto

import com.example.ticketscan.ui.screens.Category
import com.example.ticketscan.ui.screens.Item

data class CategoryDto(
    val id: String,
    val title: String,
    val elements: List<ItemDto>
)

fun CategoryDto.toDomain(): Category {
    return Category(
        name = title,
        items = elements.map { it.toDomain() }
    )
}

fun ItemDto.toDomain(): Item {
    return Item(
        name = label,
        route = "detalle/$id", // Ejemplo: generamos route en base al id
        quantity = quantity
    )
}