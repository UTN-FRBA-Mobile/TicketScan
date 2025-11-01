package com.example.ticketscan.domain.notifications

import com.example.ticketscan.domain.model.notifications.NotificationPayload
import com.example.ticketscan.domain.model.notifications.NotificationPreferences
import com.example.ticketscan.domain.model.notifications.NotificationType
import com.example.ticketscan.domain.repositories.notifications.NotificationDispatcher
import com.example.ticketscan.domain.repositories.notifications.NotificationPreferencesRepository
import com.example.ticketscan.domain.repositories.notifications.NotificationScheduler
import com.example.ticketscan.domain.repositories.stats.StatsRepository
import com.example.ticketscan.domain.repositories.ticket.TicketRepository
import com.example.ticketscan.ui.components.Period
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.util.Calendar

class NotificationCoordinator(
    private val ticketRepository: TicketRepository,
    private val statsRepository: StatsRepository,
    private val preferencesRepository: NotificationPreferencesRepository,
    private val dispatcher: NotificationDispatcher,
    private val scheduler: NotificationScheduler = NotificationScheduler.NoOp
) {

    val preferences = preferencesRepository.preferences

    suspend fun initialize() {
        val current = preferencesRepository.current()
        dispatcher.syncDevice(current)
        scheduler.reschedule(current)
    }

    suspend fun currentPreferences(): NotificationPreferences = preferencesRepository.current()

    suspend fun setWeeklyStatsEnabled(enabled: Boolean) {
        preferencesRepository.setWeeklyStatsEnabled(enabled)
        onPreferenceChanged()
    }

    suspend fun setWeeklyInactivityEnabled(enabled: Boolean) {
        preferencesRepository.setWeeklyInactivityEnabled(enabled)
        onPreferenceChanged()
    }

    suspend fun setMonthlyComparisonEnabled(enabled: Boolean) {
        preferencesRepository.setMonthlyComparisonEnabled(enabled)
        onPreferenceChanged()
    }

    suspend fun sendWeeklyStats(): Boolean {
        val currentStats = statsRepository.getCategoryStats(Period.SEMANAL, periodOffset = 0)
        val previousStats = statsRepository.getCategoryStats(Period.SEMANAL, periodOffset = 1)
        val totalSpent = currentStats.fold(BigDecimal.ZERO) { acc, stat -> acc + stat.amount }
        val previousTotal = previousStats.fold(BigDecimal.ZERO) { acc, stat -> acc + stat.amount }
        val calendar = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -7) }
        val currentWeekTickets = ticketRepository.getTicketsByFilters(categoryName = null, minDate = calendar.time)
        val ticketCount = currentWeekTickets.size
        val averagePerTicket = if (ticketCount > 0) {
            totalSpent.divide(BigDecimal.valueOf(ticketCount.toLong()), 2, RoundingMode.HALF_UP)
        } else {
            BigDecimal.ZERO
        }
        val topCategory = currentStats.maxByOrNull { it.amount }?.name

        val payload = NotificationPayload.WeeklyStats(
            totalSpent = totalSpent.setScale(2, RoundingMode.HALF_UP),
            averagePerTicket = averagePerTicket.setScale(2, RoundingMode.HALF_UP),
            previousTotal = previousTotal.setScale(2, RoundingMode.HALF_UP),
            ticketCount = ticketCount,
            topCategory = topCategory
        )
        return dispatcher.dispatch(payload)
    }

    suspend fun sendMonthlyComparison(): Boolean {
        val currentStats = statsRepository.getCategoryStats(Period.MENSUAL, periodOffset = 0)
        val previousStats = statsRepository.getCategoryStats(Period.MENSUAL, periodOffset = 1)
        val currentTotal = currentStats.fold(BigDecimal.ZERO) { acc, stat -> acc + stat.amount }
        val previousTotal = previousStats.fold(BigDecimal.ZERO) { acc, stat -> acc + stat.amount }

        val payload = NotificationPayload.MonthlyComparison(
            currentTotal = currentTotal.setScale(2, RoundingMode.HALF_UP),
            previousTotal = previousTotal.setScale(2, RoundingMode.HALF_UP)
        )
        return dispatcher.dispatch(payload)
    }

    suspend fun sendWeeklyInactivityReminder(): Boolean {
        val latestTicket = ticketRepository.getAllTickets(limit = 1).firstOrNull()
        val now = Instant.now()
        val lastTicketInstant = latestTicket?.date?.toInstant()
        val daysWithoutTicket = if (lastTicketInstant == null) {
            7L
        } else {
            val elapsed = java.time.Duration.between(lastTicketInstant, now)
            elapsed.toDays()
        }
        if (daysWithoutTicket < 7L) {
            return false
        }
        val lastTicketDate = lastTicketInstant?.atZone(ZoneId.systemDefault())?.toLocalDate()
        val payload = NotificationPayload.WeeklyInactivity(
            daysWithoutTicket = daysWithoutTicket,
            lastTicketDate = lastTicketDate
        )
        return dispatcher.dispatch(payload)
    }

    private fun java.util.Date.toInstant(): Instant = Instant.ofEpochMilli(time)

    private suspend fun onPreferenceChanged() {
        val updated = preferencesRepository.current()
        dispatcher.syncDevice(updated)
        scheduler.reschedule(updated)
    }

    suspend fun sendIfEnabled(type: NotificationType): Boolean {
        val prefs = preferencesRepository.current()
        return when (type) {
            NotificationType.WEEKLY_STATS -> if (prefs.weeklyStatsEnabled) sendWeeklyStats() else false
            NotificationType.MONTHLY_COMPARISON -> if (prefs.monthlyComparisonEnabled) sendMonthlyComparison() else false
            NotificationType.WEEKLY_INACTIVITY -> if (prefs.weeklyInactivityEnabled) sendWeeklyInactivityReminder() else false
        }
    }
}
