package com.smartcity.provider.ui.main.order.notification

import android.R
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat

class NotificationUtils(base: Context) : ContextWrapper(base) {

    val   ANDROID_CHANNEL_ID:String  = "com.smartcity.provider.ui.main.order.notification"
    val   ANDROID_CHANNEL_NAME:String  = "ANDROID CHANNEL"
    val NOTIFICATION_ID = 100

    fun showNotificationMessage(
        title: String?,
        body: String?
    ){
        val mBuilder = NotificationCompat.Builder(
            applicationContext,
            ANDROID_CHANNEL_ID
        )

        val notification=
            mBuilder
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(R.drawable.stat_notify_more)
                .setAutoCancel(true)
                .build()

        createChannels()
        getManager().notify(NOTIFICATION_ID,notification)
    }

    fun createChannels() {
        // create android channel
         if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
             val androidChannel: NotificationChannel = NotificationChannel(
                ANDROID_CHANNEL_ID,
                ANDROID_CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH
            )
            // Sets whether notifications posted to this channel should display notification lights
            androidChannel.enableLights(true);
            // Sets whether notification posted to this channel should vibrate.
            androidChannel.enableVibration(true);
            // Sets the notification light color for notifications posted to this channel
            androidChannel.setLightColor(Color.GREEN);
            // Sets whether notifications posted to this channel appear on the lockscreen or not
            androidChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            getManager().createNotificationChannel(androidChannel)

        }
    }

    fun  getManager(): NotificationManager {
        return getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }
}