package com.example.ticketscan.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.ticketscan.ui.components.CategoryPieChart
import com.example.ticketscan.ui.components.ComparisonSection
import com.example.ticketscan.ui.components.Period
import com.example.ticketscan.ui.components.PeriodSelector
import com.example.ticketscan.ui.components.TicketScanCard
import com.example.ticketscan.ui.components.TicketScanCardStyle
import com.example.ticketscan.ui.components.TicketScanScreenContainer
import com.example.ticketscan.ui.components.TicketScanSectionHeader
import com.example.ticketscan.ui.theme.TicketScanTheme

@Composable
fun StatsScreen(
    statsViewModel: StatsViewModel,
    navController: NavController,
    onCategoryClick: (String) -> Unit
) {
    val state by statsViewModel.uiState.collectAsState()
    TicketScanScreenContainer(
        verticalArrangement = Arrangement.spacedBy(TicketScanTheme.spacing.lg)
    ) {
        TicketScanSectionHeader(
            title = "Resumen de gastos",
            subtitle = "Analizá tus períodos y tendencias"
        )

        TicketScanCard(
            style = TicketScanCardStyle.Outline,
            contentPadding = PaddingValues(TicketScanTheme.spacing.lg)
        ) {
            PeriodSelector(
                selectedPeriod = state.selectedPeriod,
                onPeriodChange = { statsViewModel.onPeriodChanged(it) }
            )
        }

        val periodDescription = when (state.selectedPeriod) {
            Period.MENSUAL -> "el mes"
            Period.SEMANAL -> "la semana"
        }
        TicketScanCard(
            style = TicketScanCardStyle.Tonal,
            contentPadding = PaddingValues(TicketScanTheme.spacing.lg)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(TicketScanTheme.spacing.md)
            ) {
                Text(
                    "Comparación con $periodDescription anterior",
                    style = TicketScanTheme.typography.titleLarge,
                    color = TicketScanTheme.colors.onSurface
                )
                ComparisonSection(
                    current = state.totalAmount,
                    previous = state.previousAmount
                )
            }
        }

        TicketScanCard(
            style = TicketScanCardStyle.Filled,
            contentPadding = PaddingValues(TicketScanTheme.spacing.lg)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(TicketScanTheme.spacing.md)
            ) {
                Text(
                    "Distribución por Categoría",
                    style = TicketScanTheme.typography.titleLarge,
                    color = TicketScanTheme.colors.onSurface
                )
                CategoryPieChart(
                    stats = state.categoryStats,
                    totalAmount = state.totalAmount,
                    onCategoryClick = onCategoryClick
                )
            }
        }
    }
}
