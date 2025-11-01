package com.example.mockserver.models

import kotlinx.serialization.Serializable

@Serializable
data class Store(
    val id: String,
    val name: String,
    val cuit: Long? = null,
    val location: String? = null
)

@Serializable
data class Category(
    val id: String,
    val name: String,
    val color: String
)

@Serializable
data class TicketItem(
    val id: String,
    val name: String,
    val category: Category,
    val quantity: Int,
    val isIntUnit: Boolean,
    val price: Double
)

@Serializable
data class Ticket(
    val id: String,
    val date: String,
    val store: Store?,
    val origin: String = "MEDIA",
    val items: List<TicketItem>,
    val total: Double
)
