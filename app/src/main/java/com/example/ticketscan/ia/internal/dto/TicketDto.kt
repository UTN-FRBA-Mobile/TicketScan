package com.example.ticketscan.ia.internal.dto

import com.google.gson.annotations.SerializedName

data class TicketDto(
    @SerializedName("id") val id: String,
    @SerializedName("date") val date: String,
    @SerializedName("store") val store: StoreDto?,
    @SerializedName("origin") val origin: String,
    @SerializedName("items") val items: List<TicketItemDto>,
    @SerializedName("total") val total: Double
)

data class StoreDto(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("cuit") val cuit: Long?,
    @SerializedName("location") val location: String?
)

data class CategoryDto(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("color") val color: String
)

data class TicketItemDto(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("category") val category: CategoryDto,
    @SerializedName("quantity") val quantity: Int,
    @SerializedName("isIntUnit") val isIntUnit: Boolean,
    @SerializedName("price") val price: Double
)
