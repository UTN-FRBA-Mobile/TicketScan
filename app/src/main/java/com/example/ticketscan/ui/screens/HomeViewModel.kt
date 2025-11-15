package com.example.ticketscan.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.ticketscan.domain.model.Ticket
import com.example.ticketscan.domain.model.TicketFilter
import com.example.ticketscan.domain.viewmodel.RepositoryViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: RepositoryViewModel
) : ViewModel() {
    private val _tickets = MutableStateFlow<List<Ticket>>(emptyList())
    val tickets: StateFlow<List<Ticket>> = _tickets

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        loadTickets()
        
        // Observe ticket changes to refresh filtered results
        repository.ticketsChanged
            .onEach { loadTickets() }
            .launchIn(viewModelScope)
    }

    private fun loadTickets() {
        viewModelScope.launch {
            _tickets.value = repository.getAllTickets()
        }
    }

    fun applyFilters(filter: TicketFilter) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.searchTickets(filter) { filteredTickets ->
                _tickets.value = filteredTickets
                _isLoading.value = false
            }
        }
    }
}

class HomeViewModelFactory(private val repository: RepositoryViewModel) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
