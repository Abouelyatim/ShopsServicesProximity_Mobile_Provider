package com.smartcity.provider.ui.main.account.viewmodel

fun AccountViewModel.getNotificationSettings(): List<String> {
    getCurrentViewStateOrNew().let {
        return it.notificationSettings
    }
}