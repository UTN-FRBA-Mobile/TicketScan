package com.example.ticketscan.data.notifications

import android.content.Context
import com.example.ticketscan.domain.notifications.NotificationCoordinator
import com.example.ticketscan.domain.repositories.category.CategoryRepositorySQLite
import com.example.ticketscan.domain.repositories.icon.IconRepositorySQLite
import com.example.ticketscan.domain.repositories.notifications.NotificationScheduler
import com.example.ticketscan.domain.repositories.stats.StatsRepositorySQLite
import com.example.ticketscan.domain.repositories.ticket.TicketRepositorySQLite
import com.example.ticketscan.domain.repositories.ticketitem.TicketItemRepositorySQLite

object NotificationServiceLocator {
    fun createCoordinator(context: Context, includeScheduler: Boolean = true): NotificationCoordinator {
        val appContext = context.applicationContext
        val categoryRepository = CategoryRepositorySQLite(appContext, IconRepositorySQLite(appContext))
        val ticketItemRepository = TicketItemRepositorySQLite(appContext, categoryRepository)
        val ticketRepository = TicketRepositorySQLite(appContext, ticketItemRepository)
        val statsRepository = StatsRepositorySQLite(appContext)
        val preferencesRepository = NotificationPreferencesRepositoryImpl(appContext)
        val dispatcher = FirebaseNotificationDispatcher(appContext)
        val scheduler = if (includeScheduler) {
            NotificationWorkScheduler(appContext)
        } else {
            NotificationScheduler.NoOp
        }
        return NotificationCoordinator(
            ticketRepository = ticketRepository,
            statsRepository = statsRepository,
            preferencesRepository = preferencesRepository,
            dispatcher = dispatcher,
            scheduler = scheduler
        )
    }
}
