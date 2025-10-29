package com.example.ticketscan.data.notifications

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.example.ticketscan.MainActivity
import com.example.ticketscan.domain.model.notifications.NotificationPayload

object NotificationIntents {
    private const val EXTRA_TYPE = "com.example.ticketscan.extra.NOTIFICATION_TYPE"
    private const val EXTRA_DATA = "com.example.ticketscan.extra.NOTIFICATION_DATA"

    fun createMainActivityIntent(context: Context, payload: NotificationPayload): Intent {
        val payloadBundle = Bundle().apply {
            payload.toMap().forEach { (key, value) ->
                putString(key, value)
            }
        }
        return Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            putExtra(EXTRA_TYPE, payload.type.id)
            putExtra(EXTRA_DATA, payloadBundle)
        }
    }

    fun extractPayload(intent: Intent?): NotificationPayload? {
        intent ?: return null
        val typeId = intent.getStringExtra(EXTRA_TYPE) ?: return null
        val dataBundle = intent.getBundleExtra(EXTRA_DATA)
        val data = mutableMapOf<String, String>()
        dataBundle?.keySet()?.forEach { key ->
            dataBundle.getString(key)?.let { data[key] = it }
        }
        data["type"] = typeId
        return NotificationPayload.fromMap(data)
    }
}
