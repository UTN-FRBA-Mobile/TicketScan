package com.example.ticketscan.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.ticketscan.domain.model.Ticket
import com.example.ticketscan.domain.repositories.stats.MonthlyExpense
import com.example.ticketscan.domain.repositories.stats.StatsRepository
import com.example.ticketscan.ui.components.Period
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class CategoryDetailsUiState(
    val categoryName: String = "",
    val monthlyExpenses: List<MonthlyExpense> = emptyList(),
    val transactions: List<Ticket> = emptyList(),
    val selectedPeriod: Period = Period.MENSUAL,
)

class CategoryDetailsViewModelFactory(
    private val statsRepository: StatsRepository,
    private val categoryName: String
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CategoryDetailsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CategoryDetailsViewModel(statsRepository, categoryName) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class CategoryDetailsViewModel(
    private val statsRepository: StatsRepository,
    private val categoryName: String
) : ViewModel() {

    private val _uiState = MutableStateFlow(CategoryDetailsUiState(categoryName = categoryName))
    val uiState: StateFlow<CategoryDetailsUiState> = _uiState

    init {
        loadMonthlyExpenses()
        loadTransactions()
    }

    fun onPeriodChanged(period: Period) {
        _uiState.value = _uiState.value.copy(selectedPeriod = period)
        loadTransactions()
    }

    private fun loadMonthlyExpenses() {
        viewModelScope.launch {
            val monthlyExpenses = statsRepository.getMonthlyCategoryHistory(categoryName)
            _uiState.value = _uiState.value.copy(monthlyExpenses = monthlyExpenses)
        }
    }

    private fun loadTransactions() {
        viewModelScope.launch {
            val transactions = statsRepository.getTransactionsForCategory(categoryName, _uiState.value.selectedPeriod)
            _uiState.value = _uiState.value.copy(transactions = transactions)
        }
    }
}
