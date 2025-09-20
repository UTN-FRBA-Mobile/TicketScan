package com.example.ticketscan.ia.example

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ticketscan.ia.internal.IAServiceImpl
import com.example.ticketscan.ia.AnalizedItem
import com.example.ticketscan.ia.IAService
import com.example.ticketscan.ia.internal.mock.MockIAApi
import kotlinx.coroutines.launch
import java.io.File

class ExampleTicketScanViewModel(
    private val iaService: IAService = IAServiceImpl(MockIAApi())
) : ViewModel() {

    fun analizeImageTicket(imagen: File, onResult: (List<AnalizedItem>) -> Unit) {
        viewModelScope.launch {
            val resultado = iaService.analizeTicketImage(imagen)
            onResult(resultado)
        }
    }

    fun analizeAudioTicket(audio: File, onResult: (List<AnalizedItem>) -> Unit) {
        viewModelScope.launch {
            val resultado = iaService.analizeTicketAudio(audio)
            onResult(resultado)
        }
    }

    fun analizeItemsTicket(items: Map<String, Double>, onResult: (List<AnalizedItem>) -> Unit) {
        viewModelScope.launch {
            val resultado = iaService.analizeTicketItems(items)
            onResult(resultado)
        }
    }
}
