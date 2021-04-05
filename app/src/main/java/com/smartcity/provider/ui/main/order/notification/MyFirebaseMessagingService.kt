package com.smartcity.provider.ui.main.order.notification

import android.content.Context
import android.content.Intent
import android.text.TextUtils
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.smartcity.provider.ui.main.MainActivity
import org.json.JSONException
import org.json.JSONObject

class MyFirebaseMessagingService : FirebaseMessagingService() {

    val TAG = "AppDebug"
    private var notificationUtils: NotificationUtils? = null

    override fun onNewToken(token: String) {
        super.onNewToken(token)

        Log.e("FCM Token", token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
            handleCustomDataMessage(
                title = remoteMessage.notification?.title!!,
                message = remoteMessage.notification?.body!!
            )

    }


    private fun handleCustomDataMessage(title: String, message: String) {
        showNotificationMessage(applicationContext, title, message)
    }

    private fun showNotificationMessage(
        context: Context,
        title: String,
        message: String
    ) {
        notificationUtils = NotificationUtils(context)
        notificationUtils?.showNotificationMessage(title, message)
    }


}