package com.example.ticketscan.ui.screens

import android.content.Intent
import androidx.lifecycle.ViewModel
import com.example.ticketscan.data.notifications.NotificationIntents
import com.example.ticketscan.domain.model.notifications.NotificationPayload
import com.example.ticketscan.domain.model.notifications.NotificationType
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class NotificationNavigationViewModel : ViewModel() {

    private val _latestByType = MutableStateFlow<Map<NotificationType, NotificationPayload>>(emptyMap())
    val latestByType: StateFlow<Map<NotificationType, NotificationPayload>> = _latestByType.asStateFlow()

    private val _navigationEvents = MutableSharedFlow<NotificationPayload>(extraBufferCapacity = 1)
    val navigationEvents: SharedFlow<NotificationPayload> = _navigationEvents.asSharedFlow()

    fun handleIntent(intent: Intent?) {
        val payload = NotificationIntents.extractPayload(intent) ?: return
        registerPayload(payload)
    }

    fun registerPayload(payload: NotificationPayload) {
        _latestByType.update { current -> current + (payload.type to payload) }
        _navigationEvents.tryEmit(payload)
    }
}
