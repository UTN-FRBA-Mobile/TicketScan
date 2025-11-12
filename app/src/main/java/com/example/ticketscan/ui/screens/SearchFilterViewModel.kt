package com.example.ticketscan.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ticketscan.domain.model.TicketFilter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.Date

data class DateRange(val start: Date?, val end: Date?)
data class AmountRange(val min: Double?, val max: Double?)

class SearchFilterViewModel : ViewModel() {
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _ticketTitleSearch = MutableStateFlow("")
    val ticketTitleSearch: StateFlow<String> = _ticketTitleSearch

    private val _selectedStore = MutableStateFlow<String?>(null)
    val selectedStore: StateFlow<String?> = _selectedStore

    private val _dateRange = MutableStateFlow<DateRange?>(null)
    val dateRange: StateFlow<DateRange?> = _dateRange

    private val _amountRange = MutableStateFlow<AmountRange?>(null)
    val amountRange: StateFlow<AmountRange?> = _amountRange

    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory: StateFlow<String?> = _selectedCategory

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun updateTicketTitleSearch(query: String) {
        _ticketTitleSearch.value = query
    }

    fun setStoreFilter(storeName: String?) {
        _selectedStore.value = storeName
    }

    fun setDateRange(range: DateRange?) {
        _dateRange.value = range
    }

    fun setAmountRange(range: AmountRange?) {
        _amountRange.value = range
    }

    fun setCategoryFilter(categoryName: String?) {
        _selectedCategory.value = categoryName
    }

    fun clearFilters() {
        _searchQuery.value = ""
        _ticketTitleSearch.value = ""
        _selectedStore.value = null
        _dateRange.value = null
        _amountRange.value = null
        _selectedCategory.value = null
    }

    fun toTicketFilter(): TicketFilter {
        return TicketFilter(
            storeName = _selectedStore.value,
            minDate = _dateRange.value?.start,
            maxDate = _dateRange.value?.end,
            minAmount = _amountRange.value?.min,
            maxAmount = _amountRange.value?.max,
            categoryName = _selectedCategory.value,
            searchQuery = _ticketTitleSearch.value.takeIf { it.isNotBlank() }
        )
    }

    fun getActiveFilterCount(): Int {
        var count = 0
        if (!_ticketTitleSearch.value.isBlank()) count++
        if (_selectedStore.value != null) count++
        if (_dateRange.value != null) count++
        if (_amountRange.value != null) count++
        if (_selectedCategory.value != null) count++
        return count
    }
}

class SearchFilterViewModelFactory : androidx.lifecycle.ViewModelProvider.Factory {
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SearchFilterViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SearchFilterViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

