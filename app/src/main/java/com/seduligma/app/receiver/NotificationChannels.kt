package com.seduligma.app.receiver

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build

object NotificationChannels {
    const val SCHEDULE_CHANNEL_ID = "schedule_status"

    fun ensureCreated(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val manager = context.getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(
            NotificationChannel(
                SCHEDULE_CHANNEL_ID,
                "Schedule status",
                NotificationManager.IMPORTANCE_DEFAULT,
            ).apply {
                description = "Shows transparent reminders and schedule status."
            },
        )
    }
}
