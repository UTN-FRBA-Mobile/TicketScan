package com.example.ticketscan.ia.internal

import com.example.ticketscan.domain.model.Ticket
import java.io.File

interface IAService {
    suspend fun analyzeTicketImage(image: File): Ticket
    suspend fun analyzeTicketAudio(audio: File): Ticket
    suspend fun analyzeTicketText(items: Map<String, Double>): Ticket
}