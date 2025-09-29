package com.example.ticketscan.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ticketscan.ia.AnalizedItem
import com.example.ticketscan.ia.internal.IAServiceImpl
import com.example.ticketscan.ia.internal.mock.MockIAApi
import com.example.ticketscan.domain.services.TicketAnalysisService
import com.example.ticketscan.domain.services.TicketAnalysisServiceImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File

class CameraScanViewModel(
    private val service: TicketAnalysisService = TicketAnalysisServiceImpl(IAServiceImpl(MockIAApi()))
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _items = MutableStateFlow<List<AnalizedItem>>(emptyList())
    val items: StateFlow<List<AnalizedItem>> = _items

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun analyzeImage(file: File) {
        _isLoading.value = true
        _error.value = null
        viewModelScope.launch {
            try {
                val result = service.analyzeTicketImage(file)
                _items.value = result
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }
}
