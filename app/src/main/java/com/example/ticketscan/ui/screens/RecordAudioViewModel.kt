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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.Date
import java.util.UUID

class RecordAudioViewModel(
    private val service: IAService,
    private val repository: RepositoryViewModel
) : ViewModel() {

    private val _isRecording = MutableStateFlow(false)
    val isRecording: StateFlow<Boolean> = _isRecording.asStateFlow()

    private val _hasAudioPermission = MutableStateFlow(false)
    val hasAudioPermission: StateFlow<Boolean> = _hasAudioPermission.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _items = MutableStateFlow<List<TicketItem>>(emptyList())
    val items: StateFlow<List<TicketItem>> = _items.asStateFlow()

    private val _canContinue = MutableStateFlow(false)
    val canContinue: StateFlow<Boolean> = _canContinue.asStateFlow()

    private val _createdTicket = MutableStateFlow<UUID?>(null)
    val createdTicket: StateFlow<UUID?> = _createdTicket

    private var audioFile: File? = null

    fun onPermissionResult(granted: Boolean) {
        _hasAudioPermission.value = granted
        if (!granted) {
            _error.value = "Se requiere permiso de grabación de audio para continuar"
        }
    }

    fun onStartRecording(file: File) {
        audioFile = file
        _isRecording.value = true
        _error.value = null
    }

    fun onStopRecording() {
        _isRecording.value = false
    }

    fun onRecordingError(message: String) {
        _error.value = message
        _isRecording.value = false
    }

    fun analyzeAudio() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                val file = audioFile ?: throw IllegalStateException("No hay archivo de audio para analizar")
                val categories = withContext(Dispatchers.IO) {
                    repository.getAllCategories()
                }

                val result = withContext(Dispatchers.IO) {
                    service.analyzeTicketAudio(file, categories)
                }

                _items.value = result
                _canContinue.value = result.isNotEmpty()
            } catch (e: Exception) {
                _error.value = "Error al analizar el audio: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun saveTicket() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                val ticket = Ticket(
                    id = UUID.randomUUID(),
                    date = Date(),
                    items = _items.value,
                    total = _items.value.sumOf { it.price },
                    store = null
                )

                repository.insertTicket(ticket) { success ->
                    if (!success) {
                        _error.value = "Error al guardar el ticket"
                    }
                }

                _createdTicket.value = ticket.id
            } catch (e: Exception) {
                _error.value = "Error al guardar el ticket: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun onTicketNavigationHandled() {
        _createdTicket.value = null
    }
}

class RecordAudioViewModelFactory(
    private val iaService: IAService,
    private val repository: RepositoryViewModel
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RecordAudioViewModel::class.java)) {
            return RecordAudioViewModel(iaService, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
