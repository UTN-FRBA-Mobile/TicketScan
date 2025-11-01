package com.example.ticketscan.data.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.example.ticketscan.R

object NotificationChannels {
    const val ANALYTICS = "ticketscan.analytics"
    const val REMINDERS = "ticketscan.reminders"

    fun register(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        val manager = context.getSystemService(NotificationManager::class.java)
        val analyticsChannel = NotificationChannel(
            ANALYTICS,
            context.getString(R.string.notification_channel_analytics_name),
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = context.getString(R.string.notification_channel_analytics_description)
        }
        val remindersChannel = NotificationChannel(
            REMINDERS,
            context.getString(R.string.notification_channel_reminders_name),
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = context.getString(R.string.notification_channel_reminders_description)
        }
        manager?.createNotificationChannels(listOf(analyticsChannel, remindersChannel))
    }
}
