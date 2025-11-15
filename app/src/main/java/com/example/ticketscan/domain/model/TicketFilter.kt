package com.example.ticketscan.domain.model

import java.util.Date

data class TicketFilter(
    val storeName: String? = null,
    val minDate: Date? = null,
    val maxDate: Date? = null,
    val minAmount: Double? = null,
    val maxAmount: Double? = null,
    val categoryName: String? = null,
    val searchQuery: String? = null
) {
    fun isEmpty(): Boolean {
        return storeName == null &&
                minDate == null &&
                maxDate == null &&
                minAmount == null &&
                maxAmount == null &&
                categoryName == null &&
                searchQuery.isNullOrBlank()
    }
}

