package com.example.ticketscan.data.notifications

import android.app.PendingIntent
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.ticketscan.R
import com.example.ticketscan.domain.model.notifications.NotificationPayload
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.NumberFormat
import java.time.format.DateTimeFormatter
import java.util.Locale

class LocalNotificationPublisher(private val context: Context) {

    private val notificationManager = NotificationManagerCompat.from(context)
    private val currencyFormatter: NumberFormat = NumberFormat.getCurrencyInstance()
    private val numberFormatter: NumberFormat = NumberFormat.getNumberInstance(Locale.getDefault()).apply {
        maximumFractionDigits = 1
        minimumFractionDigits = 1
    }
    private val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.getDefault())

    fun publish(payload: NotificationPayload) {
        val content = when (payload) {
            is NotificationPayload.WeeklyStats -> weeklyStatsContent(payload)
            is NotificationPayload.MonthlyComparison -> monthlyComparisonContent(payload)
            is NotificationPayload.WeeklyInactivity -> weeklyInactivityContent(payload)
        }

        val builder = NotificationCompat.Builder(context, payload.type.channelId)
            .setSmallIcon(R.drawable.ic_carrito)
            .setContentTitle(content.title)
            .setContentText(content.text)
            .setStyle(NotificationCompat.BigTextStyle().bigText(content.text))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(mainPendingIntent(payload))

        notificationManager.notify(payload.type.notificationId, builder.build())
    }

    private fun weeklyStatsContent(payload: NotificationPayload.WeeklyStats): Content {
        val totalLabel = currencyFormatter.format(payload.totalSpent)
        val averageLabel = currencyFormatter.format(payload.averagePerTicket)
        val body = if (!payload.topCategory.isNullOrBlank()) {
            context.getString(
                R.string.notification_weekly_stats_body_with_category,
                totalLabel,
                payload.ticketCount,
                averageLabel,
                payload.topCategory
            )
        } else {
            context.getString(
                R.string.notification_weekly_stats_body,
                totalLabel,
                payload.ticketCount,
                averageLabel
            )
        }
        return Content(
            title = context.getString(R.string.notification_weekly_stats_title),
            text = body
        )
    }

    private fun monthlyComparisonContent(payload: NotificationPayload.MonthlyComparison): Content {
        val currentLabel = currencyFormatter.format(payload.currentTotal)
        val previousLabel = currencyFormatter.format(payload.previousTotal)
        val differenceLabel = formatDifference(payload.difference)
        val variationLabel = formatVariation(payload.variationPercent)
        val body = context.getString(
            R.string.notification_monthly_comparison_body,
            currentLabel,
            previousLabel,
            differenceLabel,
            variationLabel
        )
        return Content(
            title = context.getString(R.string.notification_monthly_comparison_title),
            text = body
        )
    }

    private fun weeklyInactivityContent(payload: NotificationPayload.WeeklyInactivity): Content {
        val days = payload.daysWithoutTicket
        val lastTicket = payload.lastTicketDate?.format(dateFormatter)
        val body = if (lastTicket != null) {
            context.getString(
                R.string.notification_weekly_inactivity_body_with_date,
                days,
                lastTicket
            )
        } else {
            context.getString(R.string.notification_weekly_inactivity_body, days)
        }
        return Content(
            title = context.getString(R.string.notification_weekly_inactivity_title),
            text = body
        )
    }

    private fun formatDifference(value: BigDecimal): String {
        val sign = if (value.signum() >= 0) "+" else "-"
        val amount = currencyFormatter.format(value.abs())
        return "$sign$amount"
    }

    private fun formatVariation(value: BigDecimal): String {
        val rounded = value.setScale(1, RoundingMode.HALF_UP)
        val sign = if (rounded.signum() >= 0) "+" else "-"
        val absolute = rounded.abs()
        return context.getString(R.string.notification_variation_template, sign, numberFormatter.format(absolute))
    }

    private fun mainPendingIntent(payload: NotificationPayload): PendingIntent {
        val intent = NotificationIntents.createMainActivityIntent(context, payload)
        return PendingIntent.getActivity(
            context,
            payload.type.notificationId,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    private data class Content(val title: String, val text: String)
}
