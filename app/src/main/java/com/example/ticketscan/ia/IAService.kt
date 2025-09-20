package com.example.ticketscan.ia

import com.example.ticketscan.ia.AnalizedItem
import java.io.File

interface IAService {
    suspend fun analizeTicketImage(imagen: File): List<AnalizedItem>
    suspend fun analizeTicketAudio(audio: File): List<AnalizedItem>
    suspend fun analizeTicketItems(items: Map<String, Double>): List<AnalizedItem>
}