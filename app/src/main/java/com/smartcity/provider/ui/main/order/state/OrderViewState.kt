package com.smartcity.provider.ui.main.order.state

import android.os.Parcelable
import com.smartcity.provider.models.CustomCategory
import com.smartcity.provider.models.product.Order
import kotlinx.android.parcel.Parcelize

const val ORDER_VIEW_STATE_BUNDLE_KEY = "com.codingwithmitch.openapi.ui.main.blog.state.OrderViewState"

@Parcelize
data class OrderViewState (

    var orderFields: OrderFields = OrderFields()

): Parcelable {

    @Parcelize
    data class OrderFields(
        var orderList: List<Order> = listOf(),
        var orderAction: List<Triple<String,Int,Int>> = listOf(),
        var orderActionRecyclerPosition: Int =0
    ) : Parcelable


}








