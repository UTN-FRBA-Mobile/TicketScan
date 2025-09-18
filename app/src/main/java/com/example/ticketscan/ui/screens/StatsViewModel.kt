package com.example.ticketscan.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.ticketscan.domain.StatsRepository
import com.example.ticketscan.ui.components.CategoryStat
import com.example.ticketscan.ui.components.Period
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

/*class StatsViewModelFactory(
    private val repository: StatsRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StatsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return StatsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}*/

class StatsViewModel(
    private val repository: StatsRepository
) : ViewModel() {

    val uiState = MutableStateFlow(StatsUiState())

    init {
        loadStats()
    }

    fun onPeriodChanged(period: Period) {
        uiState.value = uiState.value.copy(selectedPeriod = period)
        loadStats()
    }

    private fun loadStats() {
        viewModelScope.launch {
            val stats = repository.getCategoryStats(uiState.value.selectedPeriod)

            val total: BigDecimal = stats
                .map { BigDecimal.valueOf(it.amount.toDouble()) }
                .reduceOrNull { acc, amount -> acc.add(amount) } ?: BigDecimal.ZERO

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
                previousAmount = total, // TODO
                categoryStats = stats
            )
        }
    }
}