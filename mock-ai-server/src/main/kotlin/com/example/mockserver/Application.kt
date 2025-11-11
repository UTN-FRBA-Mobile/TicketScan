package com.example.mockserver

import com.example.mockserver.models.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.http.*
import java.time.Instant
import java.util.*

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
        configureRouting()
    }.start(wait = true)
}

fun Application.configureRouting() {
    install(ContentNegotiation) {
        json()
    }

    routing {
        get("/health") {
            call.respond(mapOf("status" to "ok"))
        }

        post("/api/analyze/image") {
            val ticket = generateMockTicket()
            call.respond(HttpStatusCode.OK, ticket)
        }

        post("/api/analyze/audio") {
            val ticket = generateMockTicket()
            call.respond(HttpStatusCode.OK, ticket)
        }

        post("/api/analyze/text") {
            val ticket = generateMockTicket()
            call.respond(HttpStatusCode.OK, ticket)
        }
    }
}

fun generateMockTicket(): Ticket {
    // Categories without IDs - the app will match by name
    val alimentacionCategory = Category(
        id = "",
        name = "Alimentación",
        color = "#FF009688"
    )
    
    val hogarCategory = Category(
        id = "",
        name = "Hogar",
        color = "#FF9C27B0"
    )
    
    return Ticket(
        id = "",
        date = Instant.now().toString(),
        store = Store(
            id = "",
            name = "Supermercado Central",
            cuit = 30123456789L,
            location = "Av. Principal 123"
        ),
        origin = "MEDIA",
        items = listOf(
            TicketItem(
                id = "",
                name = "Pan integral",
                category = alimentacionCategory,
                quantity = 2,
                isIntUnit = true,
                price = 450.50
            ),
            TicketItem(
                id = "",
                name = "Leche entera 1L",
                category = alimentacionCategory,
                quantity = 3,
                isIntUnit = true,
                price = 380.00
            ),
            TicketItem(
                id = "",
                name = "Detergente",
                category = hogarCategory,
                quantity = 1,
                isIntUnit = true,
                price = 661.00
            )
        ),
        total = 1591.50
    )
}
