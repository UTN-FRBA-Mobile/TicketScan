package com.example.ticketscan.ia.example

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ticketscan.ia.internal.IAServiceImpl
import com.example.ticketscan.domain.Ticket
import com.example.ticketscan.ia.IAService
import com.example.ticketscan.ia.internal.mock.MockIAApi
import kotlinx.coroutines.launch
import java.io.File

class ExampleTicketScanViewModel(
    context: Context,
    private val iaService: IAService = IAServiceImpl(context, MockIAApi())
) : ViewModel() {

    fun analizeImageTicket(imagen: File, onResult: (Ticket) -> Unit) {
        viewModelScope.launch {
            val resultado = iaService.analizeTicketImage(imagen)
            onResult(resultado)
        }
    }

    fun analizeAudioTicket(audio: File, onResult: (Ticket) -> Unit) {
        viewModelScope.launch {
            val resultado = iaService.analizeTicketAudio(audio)
            onResult(resultado)
        }
    }

    fun analizeItemsTicket(items: Map<String, Double>, onResult: (Ticket) -> Unit) {
        viewModelScope.launch {
            val resultado = iaService.analizeTicketItems(items)
            onResult(resultado)
        }
    }
}
