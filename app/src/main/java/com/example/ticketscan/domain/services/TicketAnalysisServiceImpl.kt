package com.example.ticketscan.domain.services

import com.example.ticketscan.ia.AnalizedItem
import com.example.ticketscan.ia.IAService
import java.io.File

class TicketAnalysisServiceImpl(
    private val iaService: IAService
) : TicketAnalysisService {
    override suspend fun analyzeTicketImage(image: File): List<AnalizedItem> =
        iaService.analizeTicketImage(image)

    override suspend fun analyzeTicketAudio(audio: File): List<AnalizedItem> =
        iaService.analizeTicketAudio(audio)

    override suspend fun analyzeTicketItems(items: Map<String, Double>): List<AnalizedItem> =
        iaService.analizeTicketItems(items)
}
