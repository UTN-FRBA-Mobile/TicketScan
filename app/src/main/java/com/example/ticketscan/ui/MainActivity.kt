package com.example.ticketscan.ui

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.lifecycle.ViewModelProvider
import com.example.ticketscan.TicketScanApp
import com.example.ticketscan.domain.Ticket
import com.example.ticketscan.domain.AnalizedItem
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.example.ticketscan.ui.TicketViewModel

class MainActivity : ComponentActivity() {
    private lateinit var viewModel: TicketViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                return TicketViewModel(application as Application) as T
            }
        })[TicketViewModel::class.java]

        // Ejemplo: guardar un ticket con items
        val ticket = Ticket(creationDate = System.currentTimeMillis())
        val items = listOf(
            AnalizedItem(id = 0, name = "Producto A", price = 100.0, category = "Alimentos", ticketId = 0),
            AnalizedItem(id = 0, name = "Producto B", price = 50.0, category = "Bebidas", ticketId = 0)
        )
        viewModel.guardarTicketConItems(ticket, items)

        // Ejemplo: observar todos los tickets
        CoroutineScope(Dispatchers.Main).launch {
            viewModel.tickets.collectLatest { tickets ->
                if (tickets.isNotEmpty()) {
                    // Aquí puedes usar la lista de tickets y sus items
                }
            }
        }
    }
}
