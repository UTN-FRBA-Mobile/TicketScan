package com.example.ticketscan.domain.repositories.settings

import com.example.ticketscan.domain.model.AppearancePreferences
import com.example.ticketscan.domain.model.FontScale
import com.example.ticketscan.domain.model.ThemeMode
import kotlinx.coroutines.flow.Flow

interface AppearancePreferencesRepository {
    val preferences: Flow<AppearancePreferences>

    suspend fun setThemeMode(themeMode: ThemeMode)

    suspend fun setFontScale(fontScale: FontScale)
}
