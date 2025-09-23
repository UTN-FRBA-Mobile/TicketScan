package com.example.ticketscan.ia.internal

import com.example.ticketscan.domain.model.TicketItem
import java.io.File

interface IAService {
    suspend fun analyzeTicketImage(imagen: File): List<TicketItem>
    suspend fun analyzeTicketAudio(audio: File): List<TicketItem>
    suspend fun analyzeTicketItems(items: Map<String, Double>): List<TicketItem>
}