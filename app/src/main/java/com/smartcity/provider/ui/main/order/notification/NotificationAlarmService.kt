package com.smartcity.provider.ui.main.order.notification

import android.app.Service
import android.content.Intent
import android.os.IBinder

class NotificationAlarmService : Service() {
    private var running :Boolean=true

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
         val notificationUtils = NotificationUtils(this)


        val notification = notificationUtils.returnNotification(
            "New Orders",
            "Click here!",
            false,
            false,
            false
        )

        startForeground(1, notification)

        object : Thread() {
            override fun run() {
                while (running){
                    sleep(10000)
                    notificationUtils.showNotificationMessage(
                        "New Orders",
                        "Click here!",
                        false,
                        false,
                        true
                    )
                }
            }
        }.start()

        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        running = false
        super.onDestroy()
    }
}