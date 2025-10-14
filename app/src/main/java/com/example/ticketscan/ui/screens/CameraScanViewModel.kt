package com.example.ticketscan.ui.screens

import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.ticketscan.domain.model.Ticket
import com.example.ticketscan.domain.model.TicketItem
import com.example.ticketscan.domain.viewmodel.RepositoryViewModel
import com.example.ticketscan.ia.internal.IAService
import com.example.ticketscan.ia.internal.IAServiceImpl
import com.example.ticketscan.ia.internal.mock.MockIAApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File
import java.util.Date
import java.util.UUID

class CameraScanViewModel(
    private val service: IAService = IAServiceImpl(MockIAApi()),
    private val repositoryViewModel: RepositoryViewModel
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _createdTicket = MutableStateFlow<UUID?>(null)
    val createdTicket: StateFlow<UUID?> = _createdTicket

    private val _items = MutableStateFlow<List<TicketItem>>(emptyList())
    val items: StateFlow<List<TicketItem>> = _items

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
                val categories = repositoryViewModel.getAllCategories()
                val result = service.analyzeTicketImage(file, categories)
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

    fun saveTicket(items: List<TicketItem>) {
        viewModelScope.launch {
            val ticket = Ticket(
                id = UUID.randomUUID(),
                date = Date(),
                items = items,
                total = items.sumOf { it.price },
                store = null
            )
            repositoryViewModel.insertTicket(
                ticket,
                onResult = { result ->
                    if (result) {
                        viewModelScope.launch {
                            _createdTicket.emit(ticket.id)
                        }
                    }
                }
            )
        }
    }

    fun onTicketNavigationHandled() {
        viewModelScope.launch {
            _createdTicket.emit(null)
        }
    }

    fun onCaptureError(message: String) {
        viewModelScope.launch {
            _error.emit(message)
        }
    }

    private fun List<TicketItem>.meetsRequirements(): Boolean =
        isNotEmpty() && all { it.name.isNotBlank() && it.price > 0.0 }

    private fun decodeThumbnail(file: File): ImageBitmap? =
        BitmapFactory.decodeFile(file.absolutePath)?.asImageBitmap()
}

class CameraScanViewModelFactory(
    private val iaService: IAService,
    private val repository: RepositoryViewModel
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CameraScanViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CameraScanViewModel(iaService, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
