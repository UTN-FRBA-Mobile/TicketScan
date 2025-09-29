package com.example.ticketscan.domain.services

import com.example.ticketscan.ia.AnalizedItem
import java.io.File

interface TicketAnalysisService {
    suspend fun analyzeTicketImage(image: File): List<AnalizedItem>
    suspend fun analyzeTicketAudio(audio: File): List<AnalizedItem>
    suspend fun analyzeTicketItems(items: Map<String, Double>): List<AnalizedItem>
}
