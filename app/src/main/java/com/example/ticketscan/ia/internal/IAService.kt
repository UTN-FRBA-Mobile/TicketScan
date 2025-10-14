package com.example.ticketscan.ia.internal

import com.example.ticketscan.domain.model.Category
import com.example.ticketscan.domain.model.TicketItem
import java.io.File

interface IAService {
    suspend fun analyzeTicketImage(imagen: File, categories: List<Category>): List<TicketItem>
    suspend fun analyzeTicketAudio(audio: File, categories: List<Category>): List<TicketItem>
    suspend fun analyzeTicketItems(items: Map<String, Double>, categories: List<Category>): List<TicketItem>
}