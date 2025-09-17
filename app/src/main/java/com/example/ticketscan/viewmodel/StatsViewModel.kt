package com.example.ticketscan.viewmodel

import androidx.lifecycle.ViewModel
import com.example.ticketscan.ui.components.CategoryStat
import com.example.ticketscan.ui.components.Period
import kotlinx.coroutines.flow.MutableStateFlow

data class StatsUiState(
    val selectedPeriod: Period = Period.MENSUAL,
    val totalAmount: Double = 0.0,
    val averageAmount: Double = 0.0,
    val categoryDistribution: List<CategoryStat> = emptyList(),
    val currentAmount: Double = 0.0,
    val previousAmount: Double = 0.0
)

class StatsViewModel : ViewModel() {
    val uiState = MutableStateFlow(StatsUiState())

    fun onPeriodChanged(period: Period) {
        // TODO Cargar los datos del período correspondiente
    }
}