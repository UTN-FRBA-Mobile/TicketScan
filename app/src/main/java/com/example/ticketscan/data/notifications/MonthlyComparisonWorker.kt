package com.example.ticketscan.data.notifications

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

class MonthlyComparisonWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        return try {
            val coordinator = NotificationServiceLocator.createCoordinator(applicationContext, includeScheduler = false)
            coordinator.sendMonthlyComparison()
            Result.success()
        } catch (ex: Exception) {
            Log.w(TAG, "Monthly comparison worker failed", ex)
            Result.retry()
        }
    }

    private companion object {
        private const val TAG = "MonthlyComparisonWrk"
    }
}
