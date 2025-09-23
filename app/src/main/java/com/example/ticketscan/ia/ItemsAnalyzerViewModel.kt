package com.example.ticketscan.ia

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ticketscan.domain.model.TicketItem
import com.example.ticketscan.ia.internal.IAService
import com.example.ticketscan.ia.internal.IAServiceImpl
import com.example.ticketscan.ia.internal.mock.MockIAApi
import kotlinx.coroutines.launch
import java.io.File

class ItemsAnalyzerViewModel(
    private val iaService: IAService = IAServiceImpl(MockIAApi())
) : ViewModel() {

    fun analyzeImageTicket(imagen: File, onResult: (List<TicketItem>) -> Unit) {
        viewModelScope.launch {
            val resultado = iaService.analyzeTicketImage(imagen)
            onResult(resultado)
        }
    }

    fun analyzeAudioTicket(audio: File, onResult: (List<TicketItem>) -> Unit) {
        viewModelScope.launch {
            val resultado = iaService.analyzeTicketAudio(audio)
            onResult(resultado)
        }
    }

    fun analyzeItemsTicket(items: Map<String, Double>, onResult: (List<TicketItem>) -> Unit) {
        viewModelScope.launch {
            val resultado = iaService.analyzeTicketItems(items)
            onResult(resultado)
        }
    }
}