package com.example.ticketscan.ui.feedback

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object UserFeedbackManager {
    private val _feedback = MutableStateFlow<UserFeedback?>(null)
    val feedback: StateFlow<UserFeedback?> = _feedback.asStateFlow()

    fun showSuccess(message: String) {
        _feedback.value = UserFeedback.Success(message)
    }

    fun showError(message: String) {
        _feedback.value = UserFeedback.Error(message)
    }

    fun showInfo(message: String) {
        _feedback.value = UserFeedback.Info(message)
    }

    fun clear() {
        _feedback.value = null
    }
}

