package com.example.ticketscan.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.ticketscan.ui.components.CategoryPieChart
import com.example.ticketscan.ui.components.PeriodSelector
import com.example.ticketscan.ui.theme.TicketScanTheme

@Composable
fun StatsScreen(
    statsViewModel: StatsViewModel,
    navController: NavController,
    onCategoryClick: (String) -> Unit
) {
    val state by statsViewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = TicketScanTheme.colors.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(16.dp)
        ) {
            Text("Resumen de gastos", style = TicketScanTheme.typography.headlineMedium)
            Spacer(Modifier.height(16.dp))

            PeriodSelector(
                selectedPeriod = state.selectedPeriod,
                onPeriodChange = { statsViewModel.onPeriodChanged(it) }
            )

            Spacer(Modifier.height(16.dp))

            /*FormattedCurrencyText(
                label = "Promedio por compra",
                amount = state.averageAmount,
                textStyle = TicketScanTheme.typography.bodyLarge
            )*/

            Spacer(Modifier.height(16.dp))

            Column(modifier = Modifier.fillMaxWidth()) {
                /*Text(
                    "Distribución por Categoría",
                    style = TicketScanTheme.typography.titleLarge
                )*/
                CategoryPieChart(
                    stats = state.categoryStats,
                    totalAmount = state.totalAmount,
                    onCategoryClick = onCategoryClick
                )
            }
        }
    }
}