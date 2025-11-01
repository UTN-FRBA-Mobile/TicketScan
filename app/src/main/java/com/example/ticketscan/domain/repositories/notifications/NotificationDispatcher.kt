package com.example.ticketscan.domain.repositories.notifications

import com.example.ticketscan.domain.model.notifications.NotificationPayload
import com.example.ticketscan.domain.model.notifications.NotificationPreferences

interface NotificationDispatcher {
    /**
     * Syncs the current device token, topic subscriptions and preference state with Firebase.
     */
    suspend fun syncDevice(preferences: NotificationPreferences)

    /**
     * Sends the desired notification to Firebase. Implementations may fallback to local delivery.
     * @return true when Firebase accepted the request.
     */
    suspend fun dispatch(payload: NotificationPayload): Boolean
}
