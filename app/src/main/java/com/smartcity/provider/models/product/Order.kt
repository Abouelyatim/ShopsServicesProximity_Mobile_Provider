package com.smartcity.provider.models.product

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Order(
    @SerializedName("id")
    @Expose
    var id:Long,

    @SerializedName("createAt")
    @Expose
    var createAt:String,

    @SerializedName("userId")
    @Expose
    var userId:Long,

    @SerializedName("orderProductVariants")
    @Expose
    var orderProductVariants: List<OrderProductVariant>

) : Parcelable {
    override fun toString(): String {
        return "Order(id=$id," +
                "createAt=$createAt," +
                "userId=$userId" +
                "orderProductVariants=$orderProductVariants)"
    }
}