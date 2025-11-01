package com.example.ticketscan.domain.repositories.notifications

import com.example.ticketscan.domain.model.notifications.NotificationPreferences

interface NotificationScheduler {
    suspend fun reschedule(preferences: NotificationPreferences)

    object NoOp : NotificationScheduler {
        override suspend fun reschedule(preferences: NotificationPreferences) = Unit
    }
}
