package com.smartcity.provider.ui.main.order.state

sealed class OrderStateEvent {

    class GetOrderEvent : OrderStateEvent()

    class GetTodayOrderEvent : OrderStateEvent()

    class GetOrderByDateEvent(
        val startDate:String,
        val endDate:String
    ) : OrderStateEvent()

    class None: OrderStateEvent()
}