package com.example.ticketscan.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.ticketscan.ui.components.AverageAmount
import com.example.ticketscan.ui.components.CategoryPieChart
import com.example.ticketscan.ui.components.ComparisonSection
import com.example.ticketscan.ui.components.PeriodSelector
import com.example.ticketscan.ui.components.TotalAmount

@Composable
fun StatsScreen(
    statsViewModel: StatsViewModel
) {
    val state by statsViewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Resumen de gastos", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(16.dp))

        PeriodSelector(
            selectedPeriod = state.selectedPeriod,
            onPeriodChange = { statsViewModel.onPeriodChanged(it) }
        )

        Spacer(Modifier.height(16.dp))

        TotalAmount(amount = state.totalAmount)
        AverageAmount(amount = state.averageAmount)

        Column(modifier = Modifier.fillMaxSize()) {
            Text(
                "Distribución por Categoría",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(16.dp)
            )
            CategoryPieChart(stats = state.categoryStats)
        }

        ComparisonSection(
            current = state.totalAmount,
            previous = state.previousAmount
        )
    }
}