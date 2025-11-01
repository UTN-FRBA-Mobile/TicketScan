package com.example.ticketscan.data.notifications

import android.util.Log
import com.example.ticketscan.domain.model.notifications.NotificationPayload
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.cancel

class TicketScanFirebaseMessagingService : FirebaseMessagingService() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val localPublisher by lazy { LocalNotificationPublisher(applicationContext) }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        val data = remoteMessage.data
        val payload = NotificationPayload.fromMap(data)
        if (payload != null) {
            localPublisher.publish(payload)
        } else {
            Log.d(TAG, "FCM message received without structured payload: $data")
        }
    }

    override fun onNewToken(token: String) {
        Log.d("FCM", "Token: $token")
        super.onNewToken(token)
        serviceScope.launch {
            runCatching {
                NotificationServiceLocator.createCoordinator(applicationContext).initialize()
            }.onFailure { Log.w(TAG, "Failed to sync notification token", it) }
        }
    }

    override fun onDestroy() {
        serviceScope.cancel()
        super.onDestroy()
    }

    private companion object {
        private const val TAG = "TicketScanFcmService"
    }
}
