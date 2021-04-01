package com.smartcity.provider.ui.main.order.state

import okhttp3.MultipartBody

sealed class OrderStateEvent {

    class GetOrderEvent : OrderStateEvent()

    class None: OrderStateEvent()
}