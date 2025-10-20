package com.example.ticketscan.ui.screens

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.ticketscan.data.settings.AppearancePreferencesRepositoryImpl
import com.example.ticketscan.domain.model.AppearancePreferences
import com.example.ticketscan.domain.model.FontScale
import com.example.ticketscan.domain.model.ThemeMode
import com.example.ticketscan.domain.repositories.settings.AppearancePreferencesRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AppearanceSettingsViewModel(
    private val repository: AppearancePreferencesRepository
) : ViewModel() {

    val uiState: StateFlow<AppearancePreferences> = repository.preferences
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS),
            initialValue = AppearancePreferences()
        )

    fun onThemeSelected(themeMode: ThemeMode) {
        viewModelScope.launch {
            repository.setThemeMode(themeMode)
        }
    }

    fun onFontScaleSelected(fontScale: FontScale) {
        viewModelScope.launch {
            repository.setFontScale(fontScale)
        }
    }

    private companion object {
        const val STOP_TIMEOUT_MS = 5_000L
    }
}

class AppearanceSettingsViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {

    private val repository: AppearancePreferencesRepository by lazy {
        AppearancePreferencesRepositoryImpl(context.applicationContext)
    }

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AppearanceSettingsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AppearanceSettingsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
