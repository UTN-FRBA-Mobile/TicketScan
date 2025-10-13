package com.example.ticketscan.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.ticketscan.domain.model.Ticket
import com.example.ticketscan.domain.repositories.stats.PeriodExpense
import com.example.ticketscan.domain.viewmodel.RepositoryViewModel
import com.example.ticketscan.ui.components.Period
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CategoryDetailsUiState(
    val categoryName: String = "",
    val periodExpenses: List<PeriodExpense> = emptyList(),
    val transactions: List<Ticket> = emptyList(),
    val period: Period = Period.MENSUAL,
    val maxPeriods: Int = 4
)

class CategoryDetailsViewModelFactory(
    private val repositoryViewModel: RepositoryViewModel,
    private val categoryName: String,
    private val maxPeriods: Int
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CategoryDetailsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CategoryDetailsViewModel(repositoryViewModel, categoryName, maxPeriods) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class CategoryDetailsViewModel(
    private val repositoryViewModel: RepositoryViewModel,
    private val categoryName: String,
    private val maxPeriods: Int
) : ViewModel() {

    private val _uiState = MutableStateFlow(CategoryDetailsUiState(categoryName = categoryName))
    val uiState: StateFlow<CategoryDetailsUiState> = _uiState.asStateFlow()

    init {
        loadPeriodExpenses()
        loadTransactions()
    }

    fun setPeriod(period: Period) {
        _uiState.update { it.copy(period = period) }
        loadPeriodExpenses()
        loadTransactions()
    }

    private fun loadPeriodExpenses() {
        viewModelScope.launch {
            val periodExpenses = repositoryViewModel.getPeriodCategoryHistory(categoryName, _uiState.value.period, maxPeriods)
            _uiState.update { it.copy(periodExpenses = periodExpenses) }
        }
    }

    private fun loadTransactions() {
        viewModelScope.launch {
            val transactions = repositoryViewModel.getTicketsByFilters(categoryName, _uiState.value.period, maxPeriods)
            _uiState.update { it.copy(transactions = transactions) }
        }
    }
}