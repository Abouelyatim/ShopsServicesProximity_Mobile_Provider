package com.smartcity.provider.models.product

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.smartcity.provider.models.Offer
import kotlinx.android.parcel.Parcelize

@Parcelize
data class OrderProductVariant(

    @SerializedName("id")
    @Expose
    var id: OrderProductVariantId,

    @SerializedName("productVariant")
    @Expose
    var productVariant: ProductVariants,

    @SerializedName("quantity")
    @Expose
    var quantity: Int,

    @SerializedName("productImage")
    @Expose
    var productImage: Image,

    @SerializedName("productName")
    @Expose
    var productName: String,

    @SerializedName("offer")
    @Expose
    var offer: Offer?

) : Parcelable {
    override fun toString(): String {
        return "OrderProductVariant(id=$id," +
                "productVariant=$productVariant," +
                "unit=$quantity" +
                "productImage=$productImage" +
                "offer=$offer" +
                "productName=$productName)"
    }
}