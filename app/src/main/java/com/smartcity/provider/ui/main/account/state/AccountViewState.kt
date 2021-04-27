package com.smartcity.provider.ui.main.account.state

import android.os.Parcelable
import com.smartcity.provider.models.Policy
import com.smartcity.provider.models.SelfPickUpOptions
import com.smartcity.provider.models.TaxRange
import com.smartcity.provider.models.product.ProductVariants
import kotlinx.android.parcel.Parcelize

const val ACCOUNT_VIEW_STATE_BUNDLE_KEY = "com.codingwithmitch.openapi.ui.main.account.state.AccountViewState"
@Parcelize
class AccountViewState(
    var notificationSettings:List<String> = listOf(),
    var policyConfiguration:PolicyConfiguration=PolicyConfiguration()
) : Parcelable {

    @Parcelize
    data class PolicyConfiguration(
        var delivery:Boolean?=null,
        var selfPickUpOption: SelfPickUpOptions?=null,
        var validDuration:Long?=null,
        var tax:Int?=null,
        var taxRanges:List<TaxRange> = listOf()
    ) : Parcelable

}