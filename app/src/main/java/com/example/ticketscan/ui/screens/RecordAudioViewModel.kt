package com.example.ticketscan.ui.screens

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.ticketscan.domain.model.Ticket
import com.example.ticketscan.domain.model.TicketItem
import com.example.ticketscan.domain.viewmodel.RepositoryViewModel
import com.example.ticketscan.ia.internal.IAService
import com.example.ticketscan.ui.feedback.UserFeedbackManager
import com.example.ticketscan.ui.feedback.ErrorMessageHelper
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

class RecordAudioViewModel(
    private val service: IAService,
    private val repositoryViewModel: RepositoryViewModel
) : ViewModel() {

    companion object {
        private const val TAG = "RecordAudioViewModel"
    }

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _createdTicket = MutableStateFlow<UUID?>(null)
    val createdTicket: StateFlow<UUID?> = _createdTicket

    private val _items = MutableStateFlow<List<TicketItem>>(emptyList())
    val items: StateFlow<List<TicketItem>> = _items

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    val canContinue: StateFlow<Boolean> = combine(_items, _isLoading) { items, loading ->
        !loading && items.meetsRequirements()
    }.stateIn(viewModelScope, SharingStarted.Eagerly, false)

    fun analyzeAudio(file: File) {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.emit(true)
            _error.emit(null)
            try {
                val categories = repositoryViewModel.getAllCategories()
                val ticket = service.analyzeTicketAudio(file, categories)
                _items.emit(ticket.items)
                UserFeedbackManager.showSuccess("Audio analizado correctamente")
            } catch (e: Exception) {
                Log.e(TAG, "Error analyzing audio", e)
                val friendlyMessage = ErrorMessageHelper.getFriendlyErrorMessage(e)
                _error.emit(friendlyMessage)
                UserFeedbackManager.showError(friendlyMessage)
            } finally {
                if (file.exists()) {
                    // Opcional: eliminar el archivo temporal para no acumular
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
                        UserFeedbackManager.showSuccess("Ticket guardado correctamente")
                        viewModelScope.launch {
                            _createdTicket.emit(ticket.id)
                        }
                    } else {
                        val errorMessage = "Error al guardar el ticket"
                        viewModelScope.launch {
                            _error.emit(errorMessage)
                        }
                        UserFeedbackManager.showError(errorMessage)
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

    fun onAnalyzeError(message: String) {
        viewModelScope.launch {
            _error.emit(message)
            UserFeedbackManager.showError(message)
        }
    }

    // Nuevo: limpia estado transitorio
    fun resetState() {
        viewModelScope.launch {
            _items.emit(emptyList())
            _error.emit(null)
        }
    }

    private fun List<TicketItem>.meetsRequirements(): Boolean =
        isNotEmpty() && all { it.name.isNotBlank() && it.price > 0.0 }
}

class RecordAudioViewModelFactory(
    private val iaService: IAService,
    private val repository: RepositoryViewModel
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RecordAudioViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RecordAudioViewModel(iaService, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
