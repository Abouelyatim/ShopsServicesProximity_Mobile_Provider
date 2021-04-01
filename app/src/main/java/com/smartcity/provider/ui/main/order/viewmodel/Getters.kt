package com.smartcity.provider.ui.main.order.viewmodel

import com.smartcity.provider.models.product.Order


fun OrderViewModel.getOrderList(): List<Order> {
    getCurrentViewStateOrNew().let {
        return it.orderFields.orderList
    }
}








