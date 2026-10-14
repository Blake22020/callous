package com.goggins.discipline

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build

class GogginsApp : Application() {
    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "goggins_reminder",
                "Goggins Reminder",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Напоминания о дисциплине. Stay hard."
            }
            val nm = getSystemService(NotificationManager::class.java)
            nm.createNotificationChannel(channel)
        }
    }

    companion object {
        const val CHANNEL_ID = "goggins_reminder"
    }
}
