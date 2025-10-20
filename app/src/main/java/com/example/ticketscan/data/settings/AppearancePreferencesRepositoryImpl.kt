package com.example.ticketscan.data.settings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.ticketscan.domain.model.AppearancePreferences
import com.example.ticketscan.domain.model.FontScale
import com.example.ticketscan.domain.model.ThemeMode
import com.example.ticketscan.domain.repositories.settings.AppearancePreferencesRepository
import java.io.IOException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

private val Context.appearancePreferencesDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "appearance_preferences"
)

class AppearancePreferencesRepositoryImpl(
    context: Context
) : AppearancePreferencesRepository {

    private val dataStore: DataStore<Preferences> = context.applicationContext.appearancePreferencesDataStore

    override val preferences: Flow<AppearancePreferences> =
        dataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map { prefs ->
                AppearancePreferences(
                    themeMode = prefs[THEME_MODE_KEY]?.let(::themeFromPref) ?: DEFAULT.themeMode,
                    fontScale = prefs[FONT_SCALE_KEY]?.let(::fontScaleFromPref) ?: DEFAULT.fontScale
                )
            }

    override suspend fun setThemeMode(themeMode: ThemeMode) {
        dataStore.edit { preferences ->
            preferences[THEME_MODE_KEY] = themeMode.name
        }
    }

    override suspend fun setFontScale(fontScale: FontScale) {
        dataStore.edit { preferences ->
            preferences[FONT_SCALE_KEY] = fontScale.name
        }
    }

    private companion object {
        val DEFAULT = AppearancePreferences()
        val THEME_MODE_KEY = stringPreferencesKey("theme_mode")
        val FONT_SCALE_KEY = stringPreferencesKey("font_scale")

        fun themeFromPref(raw: String): ThemeMode =
            runCatching { ThemeMode.valueOf(raw) }.getOrElse { DEFAULT.themeMode }

        fun fontScaleFromPref(raw: String): FontScale =
            runCatching { FontScale.valueOf(raw) }.getOrElse { DEFAULT.fontScale }
    }
}
