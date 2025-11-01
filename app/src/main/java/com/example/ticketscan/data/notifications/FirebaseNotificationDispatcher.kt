package com.example.ticketscan.data.notifications

import android.content.Context
import android.util.Log
import com.example.ticketscan.domain.model.notifications.NotificationPayload
import com.example.ticketscan.domain.model.notifications.NotificationPreferences
import com.example.ticketscan.domain.model.notifications.NotificationType
import com.example.ticketscan.domain.repositories.notifications.NotificationDispatcher
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.tasks.await

class FirebaseNotificationDispatcher(
    context: Context,
    private val messaging: FirebaseMessaging = FirebaseMessaging.getInstance(),
    private val functions: FirebaseFunctions = FirebaseFunctions.getInstance(),
    private val localPublisher: LocalNotificationPublisher = LocalNotificationPublisher(context.applicationContext)
) : NotificationDispatcher {

    override suspend fun syncDevice(preferences: NotificationPreferences) {
        val token = runCatching { messaging.token.await() }
            .onFailure { Log.w(TAG, "Unable to fetch FCM token", it) }
            .getOrNull() ?: return

        updateTopics(preferences)
        publishSyncRequest(token, preferences)
    }

    override suspend fun dispatch(payload: NotificationPayload): Boolean {
        val remoteResult = runCatching {
            val request = mapOf(
                "topic" to payload.type.topic,
                "type" to payload.type.id,
                "data" to payload.toMap()
            )
            functions.getHttpsCallable(SEND_FUNCTION_NAME).call(request).await()
        }

        if (remoteResult.isFailure) {
            Log.w(TAG, "Remote notification dispatch failed; falling back to local notification", remoteResult.exceptionOrNull())
            localPublisher.publish(payload)
            return false
        }
        return true
    }

    private suspend fun updateTopics(preferences: NotificationPreferences) {
        toggleTopic(NotificationType.WEEKLY_INACTIVITY, preferences.weeklyInactivityEnabled)
        toggleTopic(NotificationType.WEEKLY_STATS, preferences.weeklyStatsEnabled)
        toggleTopic(NotificationType.MONTHLY_COMPARISON, preferences.monthlyComparisonEnabled)
    }

    private suspend fun toggleTopic(type: NotificationType, enabled: Boolean) {
        val task = if (enabled) {
            messaging.subscribeToTopic(type.topic)
        } else {
            messaging.unsubscribeFromTopic(type.topic)
        }
        runCatching { task.await() }
            .onFailure { Log.w(TAG, "Unable to update topic ${type.topic}", it) }
    }

    private suspend fun publishSyncRequest(token: String, preferences: NotificationPreferences) {
        val payload = mapOf(
            "token" to token,
            "topics" to mapOf(
                NotificationType.WEEKLY_INACTIVITY.id to preferences.weeklyInactivityEnabled,
                NotificationType.WEEKLY_STATS.id to preferences.weeklyStatsEnabled,
                NotificationType.MONTHLY_COMPARISON.id to preferences.monthlyComparisonEnabled
            )
        )
        runCatching {
            functions.getHttpsCallable(SYNC_FUNCTION_NAME).call(payload).await()
        }.onFailure { Log.w(TAG, "Failed to sync notification preferences", it) }
    }

    private companion object {
        private const val TAG = "FirebaseNotificationDisp"
        private const val SYNC_FUNCTION_NAME = "syncNotificationPreferences"
        private const val SEND_FUNCTION_NAME = "sendTicketScanNotification"
    }
}
