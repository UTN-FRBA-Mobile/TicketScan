package com.example.ticketscan

import android.app.Application
import com.example.ticketscan.data.notifications.NotificationChannels
import com.example.ticketscan.data.notifications.NotificationServiceLocator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class TicketScanApplication : Application() {

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    override fun onCreate() {
        super.onCreate()
        NotificationChannels.register(this)
        applicationScope.launch {
            runCatching {
                NotificationServiceLocator.createCoordinator(this@TicketScanApplication).initialize()
            }
        }
    }

    override fun onTerminate() {
        applicationScope.cancel()
        super.onTerminate()
    }
}
