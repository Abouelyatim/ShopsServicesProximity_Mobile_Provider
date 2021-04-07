package com.smartcity.provider.ui.main.account.state

sealed class AccountStateEvent {

    class SaveNotificationSettings(
        val settings:List<String>
    ):AccountStateEvent()

    class GetNotificationSettings():AccountStateEvent()

    class None: AccountStateEvent()
}