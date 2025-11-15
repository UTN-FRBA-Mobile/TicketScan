package com.example.ticketscan.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.ticketscan.domain.model.OnboardingPage
import com.example.ticketscan.domain.repositories.OnboardingRepository
import com.example.ticketscan.ui.theme.TicketScanIcons
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class OnboardingViewModel(
    private val onboardingRepository: OnboardingRepository
) : ViewModel() {

    private val _currentPageIndex = MutableStateFlow(0)
    val currentPageIndex: StateFlow<Int> = _currentPageIndex

    val pages: List<OnboardingPage> = listOf(
        OnboardingPage(
            title = "¡Bienvenido a TicketScan!",
            description = "Registrá tus compras de supermercado de manera fácil y rápida. Escaneá tickets, ingresá manualmente o dictá tus compras.",
            icon = TicketScanIcons.Home
        ),
        OnboardingPage(
            title = "Escaneá tus tickets",
            description = "Tomá una foto de tu ticket y la aplicación extraerá automáticamente todos los productos y precios.",
            icon = TicketScanIcons.Camera
        ),
        OnboardingPage(
            title = "Dictá tus compras",
            description = "Grabá un audio describiendo tus compras y la aplicación las procesará automáticamente.",
            icon = TicketScanIcons.Audio
        ),
        OnboardingPage(
            title = "Ingresá manualmente",
            description = "Agregá tus compras manualmente con todos los detalles que necesites.",
            icon = TicketScanIcons.Text
        ),
        OnboardingPage(
            title = "Analizá tus gastos",
            description = "Revisá estadísticas, compará períodos y entendé mejor tus hábitos de consumo.",
            icon = TicketScanIcons.Expenses
        )
    )

    fun nextPage() {
        if (_currentPageIndex.value < pages.size - 1) {
            _currentPageIndex.value++
        }
    }

    fun previousPage() {
        if (_currentPageIndex.value > 0) {
            _currentPageIndex.value--
        }
    }

    fun skipOnboarding() {
        completeOnboarding()
    }

    fun completeOnboarding() {
        viewModelScope.launch {
            onboardingRepository.setOnboardingCompleted(true)
        }
    }

    val isLastPage: Boolean
        get() = _currentPageIndex.value == pages.size - 1

    val isFirstPage: Boolean
        get() = _currentPageIndex.value == 0
}

class OnboardingViewModelFactory(
    private val onboardingRepository: OnboardingRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(OnboardingViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return OnboardingViewModel(onboardingRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

