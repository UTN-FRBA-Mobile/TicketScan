package com.example.ticketscan.domain.model

import androidx.compose.ui.graphics.vector.ImageVector

data class OnboardingPage(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val illustration: Int? = null
)

