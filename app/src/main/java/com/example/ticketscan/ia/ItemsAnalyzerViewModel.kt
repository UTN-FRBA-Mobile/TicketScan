package com.example.ticketscan.ia

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ticketscan.domain.model.TicketItem
import com.example.ticketscan.domain.viewmodel.RepositoryViewModel
import com.example.ticketscan.ia.internal.IAService
import com.example.ticketscan.ia.internal.IAServiceImpl
import com.example.ticketscan.ia.internal.mock.MockIAApi
import kotlinx.coroutines.launch
import java.io.File

class ItemsAnalyzerViewModel(
    private val iaService: IAService = IAServiceImpl(MockIAApi()),
    private val repositoryViewModel: RepositoryViewModel
) : ViewModel() {

    fun analyzeImageTicket(imagen: File, onResult: (List<TicketItem>) -> Unit) {
        viewModelScope.launch {
            val categories = repositoryViewModel.getAllCategories()
            val resultado = iaService.analyzeTicketImage(imagen, categories)
            onResult(resultado)
        }
    }

    fun analyzeAudioTicket(audio: File, onResult: (List<TicketItem>) -> Unit) {
        viewModelScope.launch {
            val categories = repositoryViewModel.getAllCategories()
            val resultado = iaService.analyzeTicketAudio(audio, categories)
            onResult(resultado)
        }
    }

    fun analyzeItemsTicket(items: Map<String, Double>, onResult: (List<TicketItem>) -> Unit) {
        viewModelScope.launch {
            val categories = repositoryViewModel.getAllCategories()
            val resultado = iaService.analyzeTicketItems(items, categories)
            onResult(resultado)
        }
    }
}