package com.example.ticketscan.data.notifications

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.ticketscan.domain.model.notifications.NotificationPreferences
import com.example.ticketscan.domain.repositories.notifications.NotificationPreferencesRepository
import java.io.IOException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.notificationPreferencesDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "notification_preferences"
)

class NotificationPreferencesRepositoryImpl(context: Context) : NotificationPreferencesRepository {

    private val dataStore: DataStore<Preferences> = context.applicationContext.notificationPreferencesDataStore

    override val preferences: Flow<NotificationPreferences> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { prefs ->
            NotificationPreferences(
                weeklyInactivityEnabled = prefs[WEEKLY_INACTIVITY_KEY] ?: DEFAULT.weeklyInactivityEnabled,
                weeklyStatsEnabled = prefs[WEEKLY_STATS_KEY] ?: DEFAULT.weeklyStatsEnabled,
                monthlyComparisonEnabled = prefs[MONTHLY_COMPARISON_KEY] ?: DEFAULT.monthlyComparisonEnabled
            )
        }

    override suspend fun current(): NotificationPreferences = preferences.first()

    override suspend fun setWeeklyInactivityEnabled(enabled: Boolean) {
        dataStore.edit { it[WEEKLY_INACTIVITY_KEY] = enabled }
    }

    override suspend fun setWeeklyStatsEnabled(enabled: Boolean) {
        dataStore.edit { it[WEEKLY_STATS_KEY] = enabled }
    }

    override suspend fun setMonthlyComparisonEnabled(enabled: Boolean) {
        dataStore.edit { it[MONTHLY_COMPARISON_KEY] = enabled }
    }

    private companion object {
        val DEFAULT = NotificationPreferences()
        val WEEKLY_INACTIVITY_KEY = booleanPreferencesKey("weekly_inactivity_enabled")
        val WEEKLY_STATS_KEY = booleanPreferencesKey("weekly_stats_enabled")
        val MONTHLY_COMPARISON_KEY = booleanPreferencesKey("monthly_comparison_enabled")
    }
}
