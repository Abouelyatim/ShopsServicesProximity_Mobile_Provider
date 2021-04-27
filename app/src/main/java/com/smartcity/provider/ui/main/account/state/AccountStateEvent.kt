package com.smartcity.provider.ui.main.account.state

import com.smartcity.provider.models.Policy

sealed class AccountStateEvent {

    class SaveNotificationSettings(
        val settings:List<String>
    ):AccountStateEvent()

    class GetNotificationSettings():AccountStateEvent()

    class SavePolicy(
        var policy: Policy
    ):AccountStateEvent()

    class None: AccountStateEvent()
}