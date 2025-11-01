package com.example.ticketscan.domain.model.notifications

/**
 * Stores the user's opt-in choices for the app notification categories.
 */
data class NotificationPreferences(
    val weeklyInactivityEnabled: Boolean = true,
    val weeklyStatsEnabled: Boolean = true,
    val monthlyComparisonEnabled: Boolean = true
)
