package com.smartcity.provider.util

class NotificationSettings {
    companion object{

        const val ORDERS_NOTIFICATION = "notification_orders"
        const val VIBRATION_NOTIFICATION = "notification_vibration"
        const val SOUND_NOTIFICATION = "notification_sound"
        val SETTINGS_NOTIFICATION= listOf<String>(ORDERS_NOTIFICATION,VIBRATION_NOTIFICATION,SOUND_NOTIFICATION)
    }
}