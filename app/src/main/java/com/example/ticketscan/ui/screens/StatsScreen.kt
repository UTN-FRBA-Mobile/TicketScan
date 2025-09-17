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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.ticketscan.ui.components.AverageAmount
import com.example.ticketscan.ui.components.CategoryPieChart
import com.example.ticketscan.ui.components.CategoryStat
import com.example.ticketscan.ui.components.ComparisonSection
import com.example.ticketscan.ui.components.PeriodSelector
import com.example.ticketscan.viewmodel.StatsViewModel
import com.example.ticketscan.ui.components.TotalAmount

@Composable
fun StatsScreen(
    statsViewModel: StatsViewModel
) {
    val state = statsViewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Resumen de compras", style = MaterialTheme.typography.headlineSmall)

        Spacer(Modifier.height(16.dp))

        // 1. Selector de período (mensual/semanal)
        PeriodSelector(
            selectedPeriod = state.value.selectedPeriod,
            onPeriodChange = { statsViewModel.onPeriodChanged(it) }
        )

        Spacer(Modifier.height(16.dp))

        // 2. Monto total
        TotalAmount(amount = state.value.totalAmount)

        // 3. Promedio por compra
        AverageAmount(amount = state.value.averageAmount)

        // 4. Distribución por categoría
        val stats = listOf(
            CategoryStat("Comida", 250f, Color(0xFFEF5350)),
            CategoryStat("Transporte", 120f, Color(0xFF42A5F5)),
            CategoryStat("Entretenimiento", 80f, Color(0xFFAB47BC)),
            CategoryStat("Otros", 50f, Color(0xFF66BB6A))
        )

        Column(modifier = Modifier.fillMaxSize()) {
            Text(
                "Distribución por Categoría",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(16.dp)
            )
            CategoryPieChart(stats = stats)
        }

        // 5. Comparación con otros períodos
        ComparisonSection(
            current = state.value.currentAmount,
            previous = state.value.previousAmount
        )
    }
}