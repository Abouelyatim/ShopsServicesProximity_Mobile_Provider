package com.smartcity.provider.ui.main.account.state

import android.os.Parcelable
import com.smartcity.provider.models.*
import com.smartcity.provider.models.product.Product
import com.smartcity.provider.models.product.ProductVariants
import kotlinx.android.parcel.Parcelize

const val ACCOUNT_VIEW_STATE_BUNDLE_KEY = "com.codingwithmitch.openapi.ui.main.account.state.AccountViewState"
@Parcelize
class AccountViewState(
    var notificationSettings:List<String> = listOf(),
    var policyConfiguration:PolicyConfiguration=PolicyConfiguration(),
    var storeInformation: StoreInformation?=null,
    var discountFields: DiscountFields =DiscountFields(),
    var discountOfferList :DiscountOfferList = DiscountOfferList()
) : Parcelable {

    @Parcelize
    data class PolicyConfiguration(
        var delivery:Boolean?=null,
        var selfPickUpOption: SelfPickUpOptions?=null,
        var validDuration:Long?=null,
        var tax:Int?=null,
        var taxRanges:List<TaxRange> = listOf()
    ) : Parcelable

    @Parcelize
    data class DiscountOfferList(
        var offersList:List<Offer> = listOf()
    ) : Parcelable

    @Parcelize
    data class DiscountFields(
        var customCategoryList: List<CustomCategory> = listOf(),
        var selectedCustomCategory:CustomCategory?=null,
        var productsList:List<Product> = ArrayList<Product>(),
        var selectedProductToSelectVariant:Product?=null,

        var selectedProductDiscount:List<Product> = listOf(),
        var rangeDiscountDate:Pair<String?,String?> =Pair(null,null),
        var discountCode:String="",
        var offerType:OfferType=OfferType.PERCENTAGE,
        var discountValuePercentage:String="%",
        var discountValueFixed:String=""
    ) : Parcelable{
        class CreateOfferError {

            companion object{

                fun mustFillAllFields(): String{
                    return "You can't create offer without fill all information."
                }

                fun none():String{
                    return "None"
                }

            }
        }

        fun isValidForCreation(): String{
            if(selectedProductDiscount.isEmpty()
                || rangeDiscountDate.first==null
                || rangeDiscountDate.second==null
                || discountCode.isBlank()
                || discountCode.isEmpty()){
                return CreateOfferError.mustFillAllFields()
            }


            if(offerType==OfferType.FIXED && (discountValueFixed.isBlank() || discountValueFixed.isEmpty())){
                return CreateOfferError.mustFillAllFields()
            }

            if (offerType==OfferType.PERCENTAGE && discountValuePercentage=="%"){
                return CreateOfferError.mustFillAllFields()
            }

            return CreateOfferError.none()
        }
    }
}