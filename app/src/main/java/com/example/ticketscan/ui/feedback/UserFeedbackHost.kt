package com.example.ticketscan.ui.feedback

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext

@Composable
fun UserFeedbackHost() {
    val context = LocalContext.current
    val feedback by UserFeedbackManager.feedback.collectAsState()

    LaunchedEffect(feedback) {
        feedback?.let { currentFeedback ->
            val message = when (currentFeedback) {
                is UserFeedback.Success -> currentFeedback.message
                is UserFeedback.Error -> currentFeedback.message
                is UserFeedback.Info -> currentFeedback.message
            }
            
            val duration = when (currentFeedback) {
                is UserFeedback.Error -> Toast.LENGTH_LONG
                else -> Toast.LENGTH_SHORT
            }
            
            Toast.makeText(context, message, duration).show()
            
            // Clear feedback after showing
            kotlinx.coroutines.delay(100)
            UserFeedbackManager.clear()
        }
    }
}

