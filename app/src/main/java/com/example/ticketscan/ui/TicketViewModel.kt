package com.example.ticketscan.ui

import androidx.lifecycle.viewModelScope
import com.example.ticketscan.data.SQLiteService
import com.example.ticketscan.domain.Ticket
import com.example.ticketscan.domain.AnalizedItem
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TicketViewModel(app: Application) : AndroidViewModel(app) {
    private val db = SQLiteService(app)
    private val _tickets = MutableStateFlow<List<Ticket>>(emptyList())
    val tickets: StateFlow<List<Ticket>> = _tickets

    fun consultarTodosLosTickets() {
        viewModelScope.launch {
            val cursor = db.readableDatabase.query("ticket", arrayOf("id"), null, null, null, null, null)
            val ticketIds = mutableListOf<Long>()
            while (cursor.moveToNext()) {
                ticketIds.add(cursor.getLong(cursor.getColumnIndexOrThrow("id")))
            }
            cursor.close()
            val tickets = ticketIds.mapNotNull { db.getTicketWithItems(it) }
            _tickets.value = tickets
        }
    }

    fun guardarTicketConItems(ticket: Ticket, items: List<AnalizedItem>) {
        viewModelScope.launch {
            val ticketId = db.insertTicket(ticket.creationDate)
            items.forEach { db.insertAnalizedItem(it.copy(ticketId = ticketId)) }
            consultarTodosLosTickets()
        }
    }
}
