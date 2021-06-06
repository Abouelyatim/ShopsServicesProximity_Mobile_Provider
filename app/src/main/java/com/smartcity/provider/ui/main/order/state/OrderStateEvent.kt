package com.smartcity.provider.ui.main.order.state

sealed class OrderStateEvent {

    class GetOrderEvent : OrderStateEvent()

    class GetTodayOrderEvent : OrderStateEvent()

    class GetOrderByDateEvent() : OrderStateEvent()

    class SetOrderAcceptedEvent(
        var id:Long
    ) : OrderStateEvent()

    class SetOrderRejectedEvent(
        var id:Long
    ) : OrderStateEvent()

    class SetOrderInProgressEvent(
        var id:Long
    ) : OrderStateEvent()

    class SetOrderReadyEvent(
        var id:Long
    ) : OrderStateEvent()

    class SetOrderDeliveredEvent(
        var id:Long
    ) : OrderStateEvent()

    class SetOrderPickedUpEvent(
        var id:Long
    ) : OrderStateEvent()

    class None: OrderStateEvent()
}