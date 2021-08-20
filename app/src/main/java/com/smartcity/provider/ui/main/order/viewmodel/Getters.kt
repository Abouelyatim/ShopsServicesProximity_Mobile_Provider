package com.smartcity.provider.ui.main.order.viewmodel

import com.smartcity.provider.models.OrderStep
import com.smartcity.provider.models.product.Order
import com.smartcity.provider.models.product.OrderType


fun OrderViewModel.getOrderList(): List<Order> {
    getCurrentViewStateOrNew().let {
        return it.orderFields.orderList
    }
}

fun OrderViewModel.getSearchOrderList(): List<Order> {
    getCurrentViewStateOrNew().let {
        return it.orderFields.searchOrderList
    }
}

fun OrderViewModel.getSelectedOrder(): Order? {
    getCurrentViewStateOrNew().let {
        return it.orderFields.selectedOrder
    }
}

fun OrderViewModel.getOrderAction(): List<Triple<String,Int,Int>> {
    getCurrentViewStateOrNew().let {
        return it.orderFields.orderAction
    }
}

fun OrderViewModel.getOrderSteps(): List<Pair<String,Int>> {
    getCurrentViewStateOrNew().let {
        return it.orderFields.orderSteps
    }
}

fun OrderViewModel.getOrderActionRecyclerPosition():Int{
    getCurrentViewStateOrNew().let {
        return it.orderFields.orderActionRecyclerPosition
    }
}

fun OrderViewModel.getOrderStepsRecyclerPosition():Int{
    getCurrentViewStateOrNew().let {
        return it.orderFields.orderStepsRecyclerPosition
    }
}

fun OrderViewModel.getSelectedSortFilter(): Triple<String,String,String>? {
    getCurrentViewStateOrNew().let {
        return it.orderFields.selectedSortFilter
    }
}

fun OrderViewModel.getSelectedTypeFilter():Triple<String,String,String>? {
    getCurrentViewStateOrNew().let {
        return it.orderFields.selectedTypeFilter
    }
}

fun OrderViewModel.getOrderStepFilter(): OrderStep {
    getCurrentViewStateOrNew().let {
        return it.orderFields.orderStepFilter
    }
}

fun OrderViewModel.getRangeDate(): Pair<String?,String?> {
    getCurrentViewStateOrNew().let {
        return it.orderFields.rangeDate
    }
}

