package com.example.ticketscan.domain.model

data class ContactInfo(
    val name: String = "Juan",
    val lastName: String = "Pérez",
    val email: String = "",
    val phone: String = ""
) {
    val fullName: String
        get() = listOf(name, lastName)
            .filter { it.isNotBlank() }
            .joinToString(" ")
}
