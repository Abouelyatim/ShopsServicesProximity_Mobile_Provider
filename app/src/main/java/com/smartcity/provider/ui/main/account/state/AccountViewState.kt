package com.smartcity.provider.ui.main.account.state

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

const val ACCOUNT_VIEW_STATE_BUNDLE_KEY = "com.codingwithmitch.openapi.ui.main.account.state.AccountViewState"
@Parcelize
class AccountViewState(
    var customCategoryRecyclerPosition:Int=0
) : Parcelable {

}