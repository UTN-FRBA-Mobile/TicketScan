package com.example.ticketscan.ia.internal

import com.example.ticketscan.domain.model.TicketItem
import java.io.File

interface IAService {
    suspend fun analizeTicketImage(imagen: File): List<TicketItem>
    suspend fun analizeTicketAudio(audio: File): List<TicketItem>
    suspend fun analizeTicketItems(items: Map<String, Double>): List<TicketItem>
}