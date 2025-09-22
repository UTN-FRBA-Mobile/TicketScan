package com.example.ticketscan

import android.app.Application
import com.example.ticketscan.data.SQLiteService

class TicketScanApp : Application() {
    lateinit var database: SQLiteService
        private set

    override fun onCreate() {
        super.onCreate()
        database = SQLiteService(this)
    }
}

