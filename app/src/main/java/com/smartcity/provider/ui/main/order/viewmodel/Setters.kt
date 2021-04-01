package com.smartcity.provider.ui.main.order.viewmodel

import com.smartcity.provider.models.product.Order


fun OrderViewModel.setOrderListData(orderList: List<Order>){
    val update = getCurrentViewStateOrNew()
    update.orderFields.orderList = orderList
    setViewState(update)
}









