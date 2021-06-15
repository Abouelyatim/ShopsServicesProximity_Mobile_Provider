package com.smartcity.provider.models

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.smartcity.provider.models.product.Address
import kotlinx.android.parcel.Parcelize

@Parcelize
class Offer(
    @SerializedName("id")
    @Expose
    var id:Long?,

    @SerializedName("discountCode")
    @Expose
    var discountCode:String?,

    @SerializedName("type")
    @Expose
    var type:OfferType?,

    @SerializedName("newPrice")
    @Expose
    var newPrice:Double?,

    @SerializedName("percentage")
    @Expose
    var percentage:Int?,

    @SerializedName("startDate")
    @Expose
    var startDate:String?,

    @SerializedName("endDate")
    @Expose
    var endDate:String?,

    @SerializedName("providerId")
    @Expose
    var providerId:Long?,

    @SerializedName("productVariants")
    @Expose
    var productVariants:List<Long>?
) : Parcelable {

    override fun toString(): String {
        return "Address(id=$id," +
                "discountCode=$discountCode," +
                "type=$type," +
                "newPrice=$newPrice," +
                "percentage=$percentage," +
                "startDate=$startDate," +
                "endDate=$endDate," +
                "providerId=$providerId," +
                "productVariants=$productVariants)"
    }

}