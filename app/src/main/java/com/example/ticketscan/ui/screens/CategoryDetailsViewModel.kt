package com.example.ticketscan.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.ticketscan.domain.repositories.stats.PeriodExpense
import com.example.ticketscan.domain.viewmodel.RepositoryViewModel
import com.example.ticketscan.ui.components.Period
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.Date
import java.util.UUID

data class CategoryDetailsUiState(
    val categoryName: String = "",
    val periodExpenses: List<PeriodExpense> = emptyList(),
    val transactions: List<CategoryTransaction> = emptyList(),
    val period: Period = Period.MENSUAL,
    val maxPeriods: Int = 4,
    val totalAmountForPeriod: BigDecimal = BigDecimal.ZERO
)

data class CategoryTransaction(
    val ticketId: UUID,
    val storeName: String?,
    val date: Date,
    val amount: BigDecimal
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
        if (period == uiState.value.period) return
        _uiState.update { it.copy(period = period) }
        loadPeriodExpenses()
        loadTransactions()
    }

    private fun loadPeriodExpenses() {
        viewModelScope.launch {
            val totalAmountForPeriod = repositoryViewModel.getPeriodCategoryHistory(categoryName, _uiState.value.period, 1)
                .firstOrNull()?.amount ?: BigDecimal.ZERO
            val periodExpenses = repositoryViewModel.getPeriodCategoryHistory(categoryName, _uiState.value.period, maxPeriods)
            _uiState.update { it.copy(periodExpenses = periodExpenses, totalAmountForPeriod = totalAmountForPeriod) }
        }
    }

    private fun loadTransactions() {
        viewModelScope.launch {
            val tickets = repositoryViewModel.getTicketsByFilters(categoryName, _uiState.value.period, maxPeriods)
            val normalizedCategory = categoryName.trim().lowercase()
            val transactions = tickets.mapNotNull { ticket ->
                val categoryTotal = ticket.items
                    .filter { it.category.name.trim().lowercase() == normalizedCategory }
                    .fold(BigDecimal.ZERO) { acc, item ->
                        acc + BigDecimal.valueOf(item.price).setScale(2, RoundingMode.HALF_UP)
                    }

                if (categoryTotal.compareTo(BigDecimal.ZERO) > 0) {
                    val normalizedTotal = categoryTotal.setScale(2, RoundingMode.HALF_UP)
                    CategoryTransaction(
                        ticketId = ticket.id,
                        storeName = ticket.store?.name,
                        date = ticket.date,
                        amount = normalizedTotal
                    )
                } else {
                    null
                }
            }
            _uiState.update { it.copy(transactions = transactions) }
        }
    }
}