package com.smartcity.provider.ui.main.order.notification

import android.annotation.TargetApi
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import androidx.core.app.NotificationCompat
import com.smartcity.provider.R
import com.smartcity.provider.ui.main.MainActivity
import kotlin.random.Random


class NotificationUtils(val base: Context) : ContextWrapper(base) {
    var NOTIFICATION_ID = 100

    init {
        NOTIFICATION_ID= Random.nextInt()
    }


    fun showNotificationMessage(
        title:String,
        body:String,
        shouldSound:Boolean,
        shouldVibrate:Boolean
    ){
        if (isOreoOrAbove()) {
            setupNotificationChannels()
        }
        makeNotification(title,body,shouldSound,shouldVibrate)
    }

    fun  getManager(): NotificationManager {
        return getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }


    @TargetApi(Build.VERSION_CODES.O)
    fun registerNormalNotificationChannel(notificationManager: NotificationManager) {
        val channel_all = NotificationChannel(
            "CHANNEL_ID_ALL",
            "CHANNEL_NAME_ALL",
            NotificationManager.IMPORTANCE_HIGH
        )
        channel_all.enableLights(true)
        channel_all.lockscreenVisibility = Notification.VISIBILITY_PUBLIC

        channel_all.enableVibration(true)
        notificationManager.createNotificationChannel(channel_all)
        val channel_sound = NotificationChannel(
            "CHANNEL_ID_SOUND",
            "CHANNEL_NAME_ALL",
            NotificationManager.IMPORTANCE_HIGH
        )
        channel_sound.enableVibration(false)
        notificationManager.createNotificationChannel(channel_sound)
        val channel_vibrate = NotificationChannel(
            "CHANNEL_ID_VIBRATE",
            "CHANNEL_NAME_ALL",
            NotificationManager.IMPORTANCE_HIGH
        )
        channel_vibrate.setSound(null, null)
        channel_vibrate.enableVibration(true)
        notificationManager.createNotificationChannel(channel_vibrate)
        val channel_none = NotificationChannel(
            "CHANNEL_ID_NONE",
            "CHANNEL_NAME_ALL",
            NotificationManager.IMPORTANCE_HIGH
        )
        channel_none.setSound(null, null)
        channel_none.enableVibration(false)
        notificationManager.createNotificationChannel(channel_none)
    }

    private fun setupNotificationChannels() {
        registerNormalNotificationChannel(getManager())
    }

    private fun playSound(){
        val mediaPlayer = MediaPlayer.create(base, R.raw.notification)
        mediaPlayer.start()
    }

    fun makeNotification(
        title:String,
        body:String,
        shouldSound:Boolean,
        shouldVibrate:Boolean
    ) {
        val builder: NotificationCompat.Builder =
            NotificationCompat.Builder(base, getChannelId(shouldSound,shouldVibrate))
                .setContentTitle(title)
                .setSmallIcon(R.mipmap.ic_stat_notify_more)
                .setContentText(body)

        val intent = Intent(base, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            base,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        builder.setContentIntent(pendingIntent)

        if (shouldSound && !shouldVibrate) {
            playSound()
            builder.setDefaults(Notification.DEFAULT_SOUND)
                .setVibrate(longArrayOf(0L))
        }
        if (shouldVibrate && !shouldSound) {
            builder.setDefaults(Notification.DEFAULT_VIBRATE)
                .setSound(null)
        }
        if (shouldSound && shouldVibrate) {
            playSound()
            builder.setDefaults(Notification.DEFAULT_ALL)
        }

        getManager().notify(NOTIFICATION_ID, builder.build())
    }
    private fun getChannelId(
        shouldSound:Boolean,
        shouldVibrate:Boolean
    ): String {
        return if (shouldSound && shouldVibrate) {
            "CHANNEL_ID_ALL"
        } else if (shouldSound && !shouldVibrate) {
            "CHANNEL_ID_SOUND"
        } else if (!shouldSound && shouldVibrate) {
            "CHANNEL_ID_VIBRATE"
        } else {
            "CHANNEL_ID_NONE"
        }
    }
    private fun isOreoOrAbove(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
    }
}