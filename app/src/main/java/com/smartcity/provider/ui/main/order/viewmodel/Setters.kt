package com.smartcity.provider.ui.main.order.viewmodel

import com.smartcity.provider.models.product.Order
import com.smartcity.provider.ui.main.store.state.StoreViewState


fun OrderViewModel.setOrderListData(orderList: List<Order>){
    val update = getCurrentViewStateOrNew()
    update.orderFields.orderList = orderList
    setViewState(update)
}


fun OrderViewModel.setOrderActionList(orderActionList: List<Triple<String,Int,Int>>){
    val update = getCurrentViewStateOrNew()
    update.orderFields.orderAction = orderActionList
    setViewState(update)
}


fun OrderViewModel.setOrderActionRecyclerPosition(postion:Int){
    val update = getCurrentViewStateOrNew()
    update.orderFields.orderActionRecyclerPosition=postion
    setViewState(update)
}

fun OrderViewModel.clearOrderList(){
    val update = getCurrentViewStateOrNew()
    update.orderFields.orderList= listOf()
    setViewState(update)
}


