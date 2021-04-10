package com.smartcity.provider.ui.main.order.notification

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.smartcity.provider.ui.auth.AuthActivity

class NotificationAlarmService : Service() {
    override fun onBind(p0: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
         val notificationUtils = NotificationUtils(this)


        val notification = notificationUtils.returnNotification(
            "service",
            "service",
            false,
            false,
            false
        )

        startForeground(1, notification)
        return super.onStartCommand(intent, flags, startId)
    }
}