package com.example.ticketscan.ui.screens

import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ticketscan.domain.services.TicketAnalysisService
import com.example.ticketscan.domain.services.TicketAnalysisServiceImpl
import com.example.ticketscan.ia.AnalizedItem
import com.example.ticketscan.ia.internal.IAServiceImpl
import com.example.ticketscan.ia.internal.mock.MockIAApi
import java.io.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CameraScanViewModel(
    private val service: TicketAnalysisService = TicketAnalysisServiceImpl(IAServiceImpl(MockIAApi()))
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _items = MutableStateFlow<List<AnalizedItem>>(emptyList())
    val items: StateFlow<List<AnalizedItem>> = _items

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _capturedThumbnail = MutableStateFlow<ImageBitmap?>(null)
    val capturedThumbnail: StateFlow<ImageBitmap?> = _capturedThumbnail

    val canContinue: StateFlow<Boolean> = combine(_items, _isLoading) { items, loading ->
        !loading && items.meetsRequirements()
    }.stateIn(viewModelScope, SharingStarted.Eagerly, false)

    fun analyzeImage(file: File) {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.emit(true)
            _error.emit(null)
            _capturedThumbnail.emit(decodeThumbnail(file))
            try {
                val result = service.analyzeTicketImage(file)
                _items.emit(result)
            } catch (e: Exception) {
                _error.emit(e.message ?: "Error al analizar la imagen")
            } finally {
                if (file.exists()) {
                    file.delete()
                }
                _isLoading.emit(false)
            }
        }
    }

    fun onCaptureError(message: String) {
        viewModelScope.launch {
            _error.emit(message)
        }
    }

    private fun List<AnalizedItem>.meetsRequirements(): Boolean =
        isNotEmpty() && all { it.name.isNotBlank() && it.price > 0.0 }

    private fun decodeThumbnail(file: File): ImageBitmap? =
        BitmapFactory.decodeFile(file.absolutePath)?.asImageBitmap()
}
