package com.example.ticketscan.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.ticketscan.ui.components.NotificationSettingItem
import com.example.ticketscan.ui.theme.TicketScanIcons
import com.example.ticketscan.ui.theme.TicketScanTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationSettingsScreen(
    navController: NavController,
    viewModel: NotificationSettingsViewModel
) {
    val uiState = viewModel.uiState.collectAsState().value

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = "Notificaciones",
                        style = TicketScanTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = TicketScanIcons.ArrowBack,
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
            if (uiState.isSyncing) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }

            NotificationSettingItem(
                title = "Resumen semanal",
                description = "Recibe un resumen con tus gastos de la semana",
                checked = uiState.weeklyStatsEnabled,
                onCheckedChange = viewModel::onWeeklyStatsChanged,
                enabled = !uiState.isSyncing
            )

            NotificationSettingItem(
                title = "Recordatorio de tickets",
                description = "Te avisamos si pasa una semana sin cargar tickets",
                checked = uiState.weeklyInactivityEnabled,
                onCheckedChange = viewModel::onWeeklyInactivityChanged,
                enabled = !uiState.isSyncing
            )

            NotificationSettingItem(
                title = "Comparacion mensual",
                description = "Compara tus gastos con el mes anterior",
                checked = uiState.monthlyComparisonEnabled,
                onCheckedChange = viewModel::onMonthlyComparisonChanged,
                enabled = !uiState.isSyncing
            )

            if (uiState.errorMessage != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = uiState.errorMessage,
                    style = TicketScanTheme.typography.bodySmall,
                    color = TicketScanTheme.colors.error
                )
            }
        }
    }
}
