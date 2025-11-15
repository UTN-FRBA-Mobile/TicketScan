package com.example.ticketscan.ui.feedback

sealed class UserFeedback {
    data class Success(val message: String) : UserFeedback()
    data class Error(val message: String) : UserFeedback()
    data class Info(val message: String) : UserFeedback()
}

