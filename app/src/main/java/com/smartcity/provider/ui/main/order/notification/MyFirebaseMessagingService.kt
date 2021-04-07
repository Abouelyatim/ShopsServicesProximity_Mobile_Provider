package com.smartcity.provider.ui.main.order.notification

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

object Events {
    val serviceEvent: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }
}

class MyFirebaseMessagingService : FirebaseMessagingService() {
    val TAG = "AppDebug"
    private var notificationUtils: NotificationUtils? = null

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.e("FCM Token", token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Events.serviceEvent.postValue("new")
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