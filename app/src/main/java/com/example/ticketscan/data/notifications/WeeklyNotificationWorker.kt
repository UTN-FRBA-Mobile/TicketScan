package com.example.ticketscan.data.notifications

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

class WeeklyNotificationWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        return try {
            val coordinator = NotificationServiceLocator.createCoordinator(applicationContext, includeScheduler = false)
            val preferences = coordinator.currentPreferences()
            if (preferences.weeklyStatsEnabled) {
                coordinator.sendWeeklyStats()
            }
            if (preferences.weeklyInactivityEnabled) {
                coordinator.sendWeeklyInactivityReminder()
            }
            Result.success()
        } catch (ex: Exception) {
            Log.w(TAG, "Weekly notification worker failed", ex)
            Result.retry()
        }
    }

    private companion object {
        private const val TAG = "WeeklyNotificationWorker"
    }
}
