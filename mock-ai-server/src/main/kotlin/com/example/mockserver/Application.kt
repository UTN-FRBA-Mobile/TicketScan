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
    return Ticket(
        id = UUID.randomUUID().toString(),
        date = Instant.now().toString(),
        store = Store(
            id = UUID.randomUUID().toString(),
            name = "Supermercado Central",
            cuit = 30123456789L,
            location = "Av. Principal 123"
        ),
        origin = "MEDIA",
        items = listOf(
            TicketItem(
                id = UUID.randomUUID().toString(),
                name = "Pan integral",
                category = Category(
                    id = UUID.randomUUID().toString(),
                    name = "Panaderia",
                    color = "#FFB74D"
                ),
                quantity = 2,
                isIntUnit = true,
                price = 450.50
            ),
            TicketItem(
                id = UUID.randomUUID().toString(),
                name = "Leche entera 1L",
                category = Category(
                    id = UUID.randomUUID().toString(),
                    name = "Lacteos",
                    color = "#64B5F6"
                ),
                quantity = 3,
                isIntUnit = true,
                price = 380.00
            )
        ),
        total = 1591.50
    )
}
