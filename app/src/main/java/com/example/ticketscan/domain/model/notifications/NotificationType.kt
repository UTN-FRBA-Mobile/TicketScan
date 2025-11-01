package com.example.ticketscan.domain.model.notifications

/**
 * Supported notification categories delivered through Firebase.
 */
enum class NotificationType(
    val id: String,
    val topic: String,
    val channelId: String,
    val notificationId: Int
) {
    WEEKLY_INACTIVITY(
        id = "weekly_inactivity",
        topic = "weekly_inactivity",
        channelId = "ticketscan.reminders",
        notificationId = 1001
    ),
    WEEKLY_STATS(
        id = "weekly_stats",
        topic = "weekly_stats",
        channelId = "ticketscan.analytics",
        notificationId = 1002
    ),
    MONTHLY_COMPARISON(
        id = "monthly_comparison",
        topic = "monthly_comparison",
        channelId = "ticketscan.analytics",
        notificationId = 1003
    );

    companion object {
        fun fromId(raw: String): NotificationType? = values().firstOrNull { it.id == raw }
    }
}
