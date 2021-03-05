package com.smartcity.provider.ui.main.store.state

import android.os.Parcelable
import com.smartcity.provider.models.AccountProperties
import com.smartcity.provider.models.CustomCategory
import com.smartcity.provider.models.product.Product
import com.smartcity.provider.ui.main.custom_category.state.CustomCategoryViewState
import kotlinx.android.parcel.Parcelize

const val ACCOUNT_VIEW_STATE_BUNDLE_KEY = "com.codingwithmitch.openapi.ui.main.account.state.AccountViewState"

@Parcelize
class StoreViewState(

    var viewCustomCategoryFields: ViewCustomCategoryFields = ViewCustomCategoryFields(),

    var viewProductList: ViewProductList = ViewProductList()

    ) : Parcelable{

    @Parcelize
    data class ViewProductList(
        var products:List<Product> = ArrayList()
    ) : Parcelable

    @Parcelize
    data class ViewCustomCategoryFields(
        var customCategoryList: List<CustomCategory> = ArrayList<CustomCategory>()
    ) : Parcelable
}