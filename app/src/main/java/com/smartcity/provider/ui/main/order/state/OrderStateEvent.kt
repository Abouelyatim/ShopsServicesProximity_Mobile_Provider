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

    class SearchOrderByReceiverEvent(
        var firstName :String,
        var lastName :String
    ) : OrderStateEvent()

    class SearchOrderByDateEvent(
        var date : String
    ) : OrderStateEvent()

    class SetOrderNoteEvent(
        var id:Long,
        var note:String
    ) : OrderStateEvent()

    class GetPastOrderEvent() : OrderStateEvent()

    class None: OrderStateEvent()
}