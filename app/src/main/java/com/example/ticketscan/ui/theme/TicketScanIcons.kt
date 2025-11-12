@file:Suppress("DEPRECATION", "unused")

package com.example.ticketscan.ui.theme

import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Audiotrack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.FlightTakeoff
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SettingsInputComponent
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.TextFields
import java.text.Normalizer

object TicketScanIcons {
    val Home: ImageVector = Icons.Filled.Home
    // Use AutoMirrored for icons that should mirror in RTL
    val Expenses: ImageVector = Icons.Filled.ReceiptLong
    val Scan: ImageVector = Icons.Filled.QrCodeScanner
    val Profile: ImageVector = Icons.Filled.AccountCircle
    val More: ImageVector = Icons.Filled.MoreHoriz
    val Audio: ImageVector = Icons.Filled.Audiotrack
    val Camera: ImageVector = Icons.Filled.CameraAlt
    val Text: ImageVector = Icons.Filled.TextFields
    val Close: ImageVector = Icons.Filled.Close
    val Edit: ImageVector = Icons.Filled.Edit
    val Share: ImageVector = Icons.Filled.Share
    val Delete: ImageVector = Icons.Filled.Delete
    val Save: ImageVector = Icons.Filled.Save
    val Add: ImageVector = Icons.Filled.Add
    // Additional icons
    val ChevronRight: ImageVector = Icons.Filled.ChevronRight
    val Notifications: ImageVector = Icons.Filled.Notifications
    val Category: ImageVector = Icons.Filled.Category
    val SettingsInputComponent: ImageVector = Icons.Filled.SettingsInputComponent
    val Description: ImageVector = Icons.Filled.Description
    val Info: ImageVector = Icons.Filled.Info
    val Search: ImageVector = Icons.Filled.Search
    val ArrowUpward: ImageVector = Icons.Filled.ArrowUpward
    val ArrowDownward: ImageVector = Icons.Filled.ArrowDownward
    val ArrowForwardIos: ImageVector = Icons.Filled.ArrowForwardIos
    val ArrowBack: ImageVector = Icons.Filled.ArrowBack
    val EmptyInbox: ImageVector = Icons.Filled.Inbox

    private val diacriticsRegex = "\\p{InCombiningDiacriticalMarks}+".toRegex()

    private val categoryIconMap: Map<String, ImageVector> = mapOf(
        "alimentacion" to Icons.Filled.Fastfood,
        "comida" to Icons.Filled.Fastfood,
        "restaurante" to Icons.Filled.Fastfood,
        "supermercado" to Icons.Filled.Storefront,
        "mercado" to Icons.Filled.Storefront,
        "transporte" to Icons.Filled.DirectionsCar,
        "movilidad" to Icons.Filled.DirectionsCar,
        "taxi" to Icons.Filled.DirectionsCar,
        "viaje" to Icons.Filled.FlightTakeoff,
        "viajes" to Icons.Filled.FlightTakeoff,
        "travel" to Icons.Filled.FlightTakeoff,
        "entretenimiento" to Icons.Filled.Movie,
        "ocio" to Icons.Filled.Movie,
        "cine" to Icons.Filled.Movie,
        "salud" to Icons.Filled.HealthAndSafety,
        "medicina" to Icons.Filled.HealthAndSafety,
        "farmacia" to Icons.Filled.HealthAndSafety,
        "hogar" to Icons.Filled.Home,
        "casa" to Icons.Filled.Home,
        "compras" to Icons.Filled.ShoppingBag,
        "shopping" to Icons.Filled.ShoppingBag,
        "ropa" to Icons.Filled.ShoppingBag,
        "tienda" to Icons.Filled.Storefront,
        "otros" to Icons.Filled.Category
    )

    private fun String.toCategoryKey(): String {
        val normalized = Normalizer.normalize(this, Normalizer.Form.NFD)
        return diacriticsRegex.replace(normalized, "").lowercase().trim()
    }

    fun categoryIcon(name: String?): ImageVector {
        if (name.isNullOrBlank()) return Icons.Filled.Category
        val key = name.toCategoryKey()
        return categoryIconMap[key] ?: when {
            "aliment" in key || "food" in key -> Icons.Filled.Fastfood
            "transpor" in key || "movil" in key || "transit" in key -> Icons.Filled.DirectionsCar
            "viaje" in key || "travel" in key || "turis" in key -> Icons.Filled.FlightTakeoff
            "entreten" in key || "ocio" in key || "movie" in key -> Icons.Filled.Movie
            "salud" in key || "medic" in key || "farm" in key -> Icons.Filled.HealthAndSafety
            "hogar" in key || "house" in key || "casa" in key -> Icons.Filled.Home
            "compr" in key || "shop" in key || "tienda" in key -> Icons.Filled.ShoppingBag
            else -> Icons.Filled.Category
        }
    }
}
