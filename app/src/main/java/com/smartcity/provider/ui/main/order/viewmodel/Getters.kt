package com.smartcity.provider.ui.main.order.viewmodel

import com.smartcity.provider.models.product.Order


fun OrderViewModel.getOrderList(): List<Order> {
    getCurrentViewStateOrNew().let {
        return it.orderFields.orderList
    }
}


fun OrderViewModel.getOrderAction(): List<Triple<String,Int,Int>> {
    getCurrentViewStateOrNew().let {
        return it.orderFields.orderAction
    }
}

fun OrderViewModel.getOrderActionRecyclerPosition():Int{
    getCurrentViewStateOrNew().let {
        return it.orderFields.orderActionRecyclerPosition
    }
}

fun OrderViewModel.getDateFilter(): String {
    getCurrentViewStateOrNew().let {
        return it.orderFields.dateFilter
    }
}

fun OrderViewModel.getAmountFilter(): String {
    getCurrentViewStateOrNew().let {
        return it.orderFields.amountFilter
    }
}

fun OrderViewModel.getRangeDate(): Pair<String?,String?> {
    getCurrentViewStateOrNew().let {
        return it.orderFields.rangeDate
    }
}

