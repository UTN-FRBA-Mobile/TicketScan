package com.example.ticketscan.domain.repositories

import kotlinx.coroutines.flow.Flow

interface OnboardingRepository {
    suspend fun isOnboardingCompleted(): Boolean
    suspend fun setOnboardingCompleted(completed: Boolean)
    val isOnboardingCompleted: Flow<Boolean>
}

