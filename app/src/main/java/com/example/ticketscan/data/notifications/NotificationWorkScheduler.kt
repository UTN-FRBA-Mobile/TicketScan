package com.example.ticketscan.data.notifications

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.ticketscan.domain.model.notifications.NotificationPreferences
import com.example.ticketscan.domain.repositories.notifications.NotificationScheduler
import java.time.DayOfWeek
import java.time.Duration
import java.time.ZonedDateTime
import java.time.temporal.TemporalAdjusters
import java.util.concurrent.TimeUnit

class NotificationWorkScheduler(context: Context) : NotificationScheduler {

    private val workManager = WorkManager.getInstance(context.applicationContext)

    override suspend fun reschedule(preferences: NotificationPreferences) {
        if (preferences.weeklyInactivityEnabled || preferences.weeklyStatsEnabled) {
            scheduleWeeklyWork()
        } else {
            workManager.cancelUniqueWork(WEEKLY_WORK_NAME)
        }

        if (preferences.monthlyComparisonEnabled) {
            scheduleMonthlyWork()
        } else {
            workManager.cancelUniqueWork(MONTHLY_WORK_NAME)
        }
    }

    private fun scheduleWeeklyWork() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        val initialDelay = calculateWeeklyInitialDelay()
        val request = PeriodicWorkRequestBuilder<WeeklyNotificationWorker>(7, TimeUnit.DAYS)
            .setConstraints(constraints)
            .setInitialDelay(initialDelay, TimeUnit.MINUTES)
            .addTag(WEEKLY_WORK_TAG)
            .build()

        workManager.enqueueUniquePeriodicWork(
            WEEKLY_WORK_NAME,
            ExistingPeriodicWorkPolicy.UPDATE,
            request
        )
    }

    private fun scheduleMonthlyWork() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        val initialDelay = calculateMonthlyInitialDelay()
        val request = PeriodicWorkRequestBuilder<MonthlyComparisonWorker>(30, TimeUnit.DAYS)
            .setConstraints(constraints)
            .setInitialDelay(initialDelay, TimeUnit.MINUTES)
            .addTag(MONTHLY_WORK_TAG)
            .build()

        workManager.enqueueUniquePeriodicWork(
            MONTHLY_WORK_NAME,
            ExistingPeriodicWorkPolicy.UPDATE,
            request
        )
    }

    private fun calculateWeeklyInitialDelay(): Long {
        val now = ZonedDateTime.now()
        var nextRun = now.with(TemporalAdjusters.nextOrSame(DayOfWeek.MONDAY))
            .withHour(DEFAULT_HOUR)
            .withMinute(0)
            .withSecond(0)
            .withNano(0)
        if (!nextRun.isAfter(now)) {
            nextRun = nextRun.plusWeeks(1)
        }
        return Duration.between(now, nextRun).toMinutes().coerceAtLeast(MIN_DELAY_MINUTES)
    }

    private fun calculateMonthlyInitialDelay(): Long {
        val now = ZonedDateTime.now()
        var nextRun = now.withDayOfMonth(1)
            .withHour(DEFAULT_HOUR)
            .withMinute(0)
            .withSecond(0)
            .withNano(0)
        if (!nextRun.isAfter(now)) {
            nextRun = nextRun.plusMonths(1)
                .withDayOfMonth(1)
                .withHour(DEFAULT_HOUR)
                .withMinute(0)
                .withSecond(0)
                .withNano(0)
        }
        return Duration.between(now, nextRun).toMinutes().coerceAtLeast(MIN_DELAY_MINUTES)
    }

    private companion object {
        private const val WEEKLY_WORK_NAME = "TicketScanWeeklyNotifications"
        private const val WEEKLY_WORK_TAG = "TicketScanWeekly" 
        private const val MONTHLY_WORK_NAME = "TicketScanMonthlyComparison"
        private const val MONTHLY_WORK_TAG = "TicketScanMonthly"
        private const val DEFAULT_HOUR = 9
        private val MIN_DELAY_MINUTES = 15L
    }
}
