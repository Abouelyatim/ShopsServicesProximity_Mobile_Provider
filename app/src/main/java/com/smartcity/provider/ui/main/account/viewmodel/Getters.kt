package com.smartcity.provider.ui.main.account.viewmodel

import com.smartcity.provider.models.*
import com.smartcity.provider.models.product.Product
import com.smartcity.provider.models.product.ProductVariants

fun AccountViewModel.getPolicyConfigurationDelivery(): Boolean? {
    getCurrentViewStateOrNew().let {
        return it.policyConfiguration.delivery
    }
}

fun AccountViewModel.getPolicyConfigurationSelfPickUpOption(): SelfPickUpOptions? {
    getCurrentViewStateOrNew().let {
        return it.policyConfiguration.selfPickUpOption
    }
}

fun AccountViewModel.getPolicyConfigurationValidDuration(): Long? {
    getCurrentViewStateOrNew().let {
        return it.policyConfiguration.validDuration
    }
}

fun AccountViewModel.getPolicyConfigurationTax(): Int? {
    getCurrentViewStateOrNew().let {
        return it.policyConfiguration.tax
    }
}

fun AccountViewModel.getPolicyConfigurationTaxRanges():List<TaxRange> {
    getCurrentViewStateOrNew().let {
        return it.policyConfiguration.taxRanges
    }
}

fun AccountViewModel.getCustomCategoryList():List<CustomCategory>{
    getCurrentViewStateOrNew().let {
        return it.discountFields.customCategoryList
    }
}

fun AccountViewModel.getSelectedCustomCategory():CustomCategory?{
    getCurrentViewStateOrNew().let {
        return it.discountFields.selectedCustomCategory
    }
}

fun AccountViewModel.getProductList():List<Product>{
    getCurrentViewStateOrNew().let {
        return it.discountFields.productsList
    }
}

fun AccountViewModel.getSelectedProductDiscount():List<Product>{
    getCurrentViewStateOrNew().let {
        return it.discountFields.selectedProductDiscount
    }
}

fun AccountViewModel.getSelectedProductToSelectVariant():Product?{
    getCurrentViewStateOrNew().let {
        return it.discountFields.selectedProductToSelectVariant
    }
}

fun AccountViewModel.getRangeDiscountDate():Pair<String?,String?>{
    getCurrentViewStateOrNew().let {
        return it.discountFields.rangeDiscountDate
    }
}

fun AccountViewModel.getDiscountCode():String{
    getCurrentViewStateOrNew().let {
        return it.discountFields.discountCode
    }
}

fun AccountViewModel.getOfferType(): OfferType {
    getCurrentViewStateOrNew().let {
        return it.discountFields.offerType
    }
}

fun AccountViewModel.getDiscountValuePercentage():String{
    getCurrentViewStateOrNew().let {
        return it.discountFields.discountValuePercentage
    }
}

fun AccountViewModel.getDiscountValueFixed():String{
    getCurrentViewStateOrNew().let {
        return it.discountFields.discountValueFixed
    }
}

fun AccountViewModel.getOffersList():List<Offer>{
    getCurrentViewStateOrNew().let {
        return it.discountOfferList.offersList
    }
}