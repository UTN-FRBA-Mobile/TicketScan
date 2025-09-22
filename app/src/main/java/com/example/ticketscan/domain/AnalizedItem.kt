package com.example.ticketscan.domain

data class AnalizedItem(
    val id: Long = 0,
    val name: String,
    val price: Double,
    val category: String,
    val ticketId: Long // Relación con Ticket
)