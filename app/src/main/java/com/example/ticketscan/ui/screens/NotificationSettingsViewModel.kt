package com.example.ticketscan.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.ticketscan.data.notifications.NotificationServiceLocator
import com.example.ticketscan.domain.notifications.NotificationCoordinator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class NotificationSettingsUiState(
    val weeklyInactivityEnabled: Boolean = true,
    val weeklyStatsEnabled: Boolean = true,
    val monthlyComparisonEnabled: Boolean = true,
    val isSyncing: Boolean = false,
    val errorMessage: String? = null
)

class NotificationSettingsViewModel(
    private val coordinator: NotificationCoordinator
) : ViewModel() {

    private val _uiState = MutableStateFlow(NotificationSettingsUiState())
    val uiState: StateFlow<NotificationSettingsUiState> = _uiState

    init {
        viewModelScope.launch {
            coordinator.preferences.collect { preferences ->
                _uiState.update { state ->
                    state.copy(
                        weeklyInactivityEnabled = preferences.weeklyInactivityEnabled,
                        weeklyStatsEnabled = preferences.weeklyStatsEnabled,
                        monthlyComparisonEnabled = preferences.monthlyComparisonEnabled
                    )
                }
            }
        }
        synchronizeDevice()
    }

    fun onWeeklyInactivityChanged(enabled: Boolean) {
        mutatePreferences { coordinator.setWeeklyInactivityEnabled(enabled) }
    }

    fun onWeeklyStatsChanged(enabled: Boolean) {
        mutatePreferences { coordinator.setWeeklyStatsEnabled(enabled) }
    }

    fun onMonthlyComparisonChanged(enabled: Boolean) {
        mutatePreferences { coordinator.setMonthlyComparisonEnabled(enabled) }
    }

    private fun synchronizeDevice() {
        mutatePreferences { coordinator.initialize() }
    }

    private fun mutatePreferences(block: suspend () -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSyncing = true, errorMessage = null) }
            val result = runCatching {
                withContext(Dispatchers.IO) { block() }
            }
            _uiState.update { state ->
                val error = result.exceptionOrNull()
                state.copy(
                    isSyncing = false,
                    errorMessage = error?.localizedMessage ?: error?.message
                )
            }
        }
    }
}

class NotificationSettingsViewModelFactory(
    private val coordinatorProvider: () -> NotificationCoordinator
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NotificationSettingsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NotificationSettingsViewModel(coordinatorProvider()) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

    companion object {
        fun fromContext(context: android.content.Context) = NotificationSettingsViewModelFactory {
            NotificationServiceLocator.createCoordinator(context)
        }
    }
}
