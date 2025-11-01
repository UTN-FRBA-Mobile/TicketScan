package com.example.ticketscan.domain.repositories.notifications

import com.example.ticketscan.domain.model.notifications.NotificationPreferences
import kotlinx.coroutines.flow.Flow

interface NotificationPreferencesRepository {
    val preferences: Flow<NotificationPreferences>

    suspend fun current(): NotificationPreferences

    suspend fun setWeeklyInactivityEnabled(enabled: Boolean)

    suspend fun setWeeklyStatsEnabled(enabled: Boolean)

    suspend fun setMonthlyComparisonEnabled(enabled: Boolean)
}
