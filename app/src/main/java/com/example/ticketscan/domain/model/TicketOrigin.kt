package com.example.ticketscan.domain.model

enum class TicketOrigin(val mode: String) {
    MEDIA("media"),
    TEXT("text"),
    AUDIO("audio");

    companion object {
        fun fromString(mode: String): TicketOrigin {
            return TicketOrigin.entries.firstOrNull { it.name.equals(mode, ignoreCase = true) } ?: TEXT
        }
    }
}
