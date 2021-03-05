package com.smartcity.provider.ui.main.store.state

import com.smartcity.provider.ui.main.custom_category.state.CustomCategoryStateEvent

sealed class StoreStateEvent{

    class CustomCategoryMain : StoreStateEvent()

    class ProductMain(
        val id: Long
    ) : StoreStateEvent()

    class AllProduct() : StoreStateEvent()

    class None: StoreStateEvent()
}