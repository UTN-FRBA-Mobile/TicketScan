package com.example.ticketscan.ui.components

import androidx.compose.ui.graphics.vector.ImageVector

data class ProfileItem(
    val text: String,
    val icon: ImageVector,
    val onClick: () -> Unit
)
