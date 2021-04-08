package com.smartcity.provider.ui.main.order.notification

import android.annotation.TargetApi
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.smartcity.provider.R
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

    private fun setupNotificationChannels() {
        registerNormalNotificationChannel(getManager())
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

        /*val intent = Intent(base, AuthActivity::class.java)
        intent.putExtra("Confirm",true)
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)

        val pendingIntent = PendingIntent.getActivity(
            base,
            0,
            intent,
            PendingIntent.FLAG_ONE_SHOT
        )
        builder.addAction(R.drawable.ic_account_circle_white_24dp,"Confirm",pendingIntent)

        builder.setContentIntent(pendingIntent)*/

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

    private fun playSound(){
        startService(Intent(this, NotificationSoundService::class.java))
    }

    internal class NotificationSoundService : Service() {
        var mediaPlayer: MediaPlayer? = null
        override fun onBind(intent: Intent?): IBinder? {
            return null
        }

        override fun onCreate() {
             mediaPlayer = MediaPlayer.create(this, R.raw.notification)
            mediaPlayer!!.isLooping = false
        }

        override fun onDestroy() {
            mediaPlayer!!.stop()
        }

        override  fun onStart(intent: Intent?, startid: Int) {
            mediaPlayer!!.start()
        }
    }

    private fun isOreoOrAbove(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
    }

    fun  getManager(): NotificationManager {
        return getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }
}