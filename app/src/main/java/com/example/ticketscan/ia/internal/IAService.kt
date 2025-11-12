package com.example.ticketscan.ia.internal

import com.example.ticketscan.domain.model.Category
import com.example.ticketscan.domain.model.Ticket
import java.io.File

interface IAService {
    suspend fun analyzeTicketImage(image: File, categories: List<Category>): Ticket
    suspend fun analyzeTicketAudio(audio: File, categories: List<Category>): Ticket
    suspend fun analyzeTicketText(items: Map<String, Double>, categories: List<Category>): Ticket
}