package com.goggins.discipline.workers

import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.goggins.discipline.GogginsApp
import com.goggins.discipline.data.content.Quotes
import com.goggins.discipline.data.settings.SettingsManager
import kotlinx.coroutines.flow.first

/**
 * Ежедневный пинок. Уведомления всегда максимум MILD,
 * чтобы не прилетело от сторов и чувствительных людей —
 * жесть живёт только внутри приложения.
 */
class ReminderWorker(appContext: Context, params: WorkerParameters) : CoroutineWorker(appContext, params) {
    override suspend fun doWork(): Result {
        val settings = SettingsManager(applicationContext).settings.first()
        if (!settings.reminderEnabled) return Result.success()
        val q = Quotes.daily(System.currentTimeMillis() / 86_400_000L)
        val name = settings.userName.ifBlank { "боец" }
        val title = "ПОДЪЁМ, $name. STAY HARD."
        val text = q.text

        if (ActivityCompat.checkSelfPermission(
                applicationContext,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // На Android 13+ без разрешения просто пропускаем, не падаем.
            return Result.success()
        }

        val nm = applicationContext.getSystemService(NotificationManager::class.java)
        val notif = NotificationCompat.Builder(applicationContext, GogginsApp.CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(text)
            .setStyle(NotificationCompat.BigTextStyle().bigText(text))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()
        nm.notify(41, notif)
        return Result.success()
    }

    companion object {
        const val TAG = "goggins_daily"
    }
}
