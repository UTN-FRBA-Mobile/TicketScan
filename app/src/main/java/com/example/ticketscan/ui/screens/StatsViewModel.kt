package com.example.ticketscan.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.ticketscan.domain.model.CategoryStat
import com.example.ticketscan.domain.viewmodel.RepositoryViewModel
import com.example.ticketscan.ui.components.Period
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.math.RoundingMode

data class StatsUiState(
    val selectedPeriod: Period = Period.MENSUAL,
    val totalAmount: BigDecimal = BigDecimal.ZERO,
    val averageAmount: BigDecimal = BigDecimal.ZERO,
    val previousAmount: BigDecimal = BigDecimal.ZERO,
    val categoryStats: List<CategoryStat> = emptyList()
)

class StatsViewModelFactory(
    private val repository: RepositoryViewModel
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StatsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return StatsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class StatsViewModel(
    private val repository: RepositoryViewModel
) : ViewModel() {

    val uiState = MutableStateFlow(StatsUiState())

    init {
        loadStats()
    }

    fun onPeriodChanged(period: Period) {
        viewModelScope.launch {
            loadStats(period)
        }
    }

    private fun loadStats(period: Period = uiState.value.selectedPeriod) {
        viewModelScope.launch {
            // Lanza ambas peticiones en paralelo para mayor eficiencia
            val currentStatsDeferred = async { repository.getCategoryStats(period, periodOffset = 0) }
            val previousStatsDeferred = async { repository.getCategoryStats(period, periodOffset = 1) }

            // Espera a que ambas peticiones terminen
            val stats = currentStatsDeferred.await()
            val previousStats = previousStatsDeferred.await()

            // Calcula el total del período actual
            val total: BigDecimal = stats.map { it.amount }.fold(BigDecimal.ZERO, BigDecimal::add)

            // Calcula el total del período anterior
            val previousTotal: BigDecimal = previousStats.map { it.amount }.fold(BigDecimal.ZERO, BigDecimal::add)

            val average: BigDecimal = if (stats.isNotEmpty()) {
                total.divide(
                    BigDecimal(stats.size),
                    2,
                    RoundingMode.HALF_UP
                )
            } else {
                BigDecimal.ZERO
            }

            uiState.value = uiState.value.copy(
                totalAmount = total,
                averageAmount = average,
                previousAmount = previousTotal,
                categoryStats = stats
            )
        }
    }
}
