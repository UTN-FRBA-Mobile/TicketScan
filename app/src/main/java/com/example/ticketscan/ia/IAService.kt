package com.example.ticketscan.ia

import com.example.ticketscan.domain.AnalizedItem
import com.example.ticketscan.domain.Ticket
import java.io.File

interface IAService {
    suspend fun analizeTicketImage(imagen: File): Ticket
    suspend fun analizeTicketAudio(audio: File): Ticket
    suspend fun analizeTicketItems(items: Map<String, Double>): Ticket
}