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

    class SetOrderReadyEvent(
        var id:Long
    ) : OrderStateEvent()

    class SetOrderDeliveredEvent(
        var id:Long,
        var comment:String?,
        var date:String?
    ) : OrderStateEvent()

    class SetOrderPickedUpEvent(
        var id:Long,
        var comment:String?,
        var date:String?
    ) : OrderStateEvent()

    class GetOrderByIdEvent(
        var orderId : Long
    ) : OrderStateEvent()

    class None: OrderStateEvent()
}