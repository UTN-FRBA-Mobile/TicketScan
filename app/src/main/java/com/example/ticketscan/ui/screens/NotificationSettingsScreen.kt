package com.example.ticketscan.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.ticketscan.ui.components.NotificationSettingItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationSettingsScreen(
    navController: NavController
) {
    var monthlyReportEnabled by remember { mutableStateOf(true) }
    var weeklyReminderEnabled by remember { mutableStateOf(true) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = "Notificaciones",
                        style = MaterialTheme.typography.titleLarge
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Monthly Report Notification
            NotificationSettingItem(
                title = "Notificación de Reporte Mensual",
                description = "Recibe un resumen mensual de tus gastos",
                checked = monthlyReportEnabled,
                onCheckedChange = { monthlyReportEnabled = it }
            )

            // Weekly Reminder Notification
            NotificationSettingItem(
                title = "Recordatorio semanal para subir Tickets",
                description = "Recuerda subir tus tickets de la semana",
                checked = weeklyReminderEnabled,
                onCheckedChange = { weeklyReminderEnabled = it }
            )
        }
    }
}
