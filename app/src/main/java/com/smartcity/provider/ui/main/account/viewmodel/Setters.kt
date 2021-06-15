package com.smartcity.provider.ui.main.account.viewmodel

import com.smartcity.provider.models.CustomCategory
import com.smartcity.provider.models.OfferType
import com.smartcity.provider.models.SelfPickUpOptions
import com.smartcity.provider.models.TaxRange
import com.smartcity.provider.models.product.Product
import com.smartcity.provider.models.product.ProductVariants
import com.smartcity.provider.ui.main.account.state.AccountViewState

fun AccountViewModel.setPolicyConfigurationDelivery(bool:Boolean){
    val update = getCurrentViewStateOrNew()
    update.policyConfiguration.delivery = bool
    setViewState(update)
}

fun AccountViewModel.setPolicyConfigurationSelfPickUpOption(selfPickUpOption: SelfPickUpOptions){
    val update = getCurrentViewStateOrNew()
    update.policyConfiguration.selfPickUpOption = selfPickUpOption
    setViewState(update)
}

fun AccountViewModel.setPolicyConfigurationValidDuration(duration:Long){
    val update = getCurrentViewStateOrNew()
    update.policyConfiguration.validDuration = duration
    setViewState(update)
}

fun AccountViewModel.setPolicyConfigurationTax(tax:Int){
    val update = getCurrentViewStateOrNew()
    update.policyConfiguration.tax = tax
    setViewState(update)
}

fun AccountViewModel.setPolicyConfigurationTaxRanges(taxRanges:List<TaxRange>){
    val update = getCurrentViewStateOrNew()
    update.policyConfiguration.taxRanges = taxRanges
    setViewState(update)
}

fun AccountViewModel.clearPolicyConfiguration(){
    val update = getCurrentViewStateOrNew()
    update.policyConfiguration= AccountViewState.PolicyConfiguration()
    setViewState(update)
}

fun AccountViewModel.setCustomCategoryList(list:List<CustomCategory>){
    val update = getCurrentViewStateOrNew()
    update.discountFields.customCategoryList = list
    setViewState(update)
}

fun AccountViewModel.setSelectedCustomCategory(customCategory: CustomCategory){
    val update = getCurrentViewStateOrNew()
    update.discountFields.selectedCustomCategory = customCategory
    setViewState(update)
}

fun AccountViewModel.setProductList(products: List<Product>){
    val update = getCurrentViewStateOrNew()
    update.discountFields.productsList = products
    setViewState(update)
}

fun AccountViewModel.clearProductList(){
    val update = getCurrentViewStateOrNew()
    update.discountFields.productsList = listOf()
    setViewState(update)
}

fun AccountViewModel.setSelectedProductDiscount(list:List<Product>){
    val update = getCurrentViewStateOrNew()
    update.discountFields.selectedProductDiscount = list
    setViewState(update)
}

fun AccountViewModel.setSelectedProductToSelectVariant(product: Product){
    val update = getCurrentViewStateOrNew()
    update.discountFields.selectedProductToSelectVariant = product
    setViewState(update)
}

fun AccountViewModel.clearSelectedProductToSelectVariant(){
    val update = getCurrentViewStateOrNew()
    update.discountFields.selectedProductToSelectVariant = null
    setViewState(update)
}

fun AccountViewModel.setStartRangeDiscountDate(date:String){
    val update = getCurrentViewStateOrNew()
    val second=update.discountFields.rangeDiscountDate.second
    update.discountFields.rangeDiscountDate= Pair(date,second)
    setViewState(update)
}

fun AccountViewModel.setEndRangeDiscountDate(date:String){
    val update = getCurrentViewStateOrNew()
    val first=update.discountFields.rangeDiscountDate.first
    update.discountFields.rangeDiscountDate=Pair(first,date)
    setViewState(update)
}

fun AccountViewModel.setDiscountCode(code:String){
    val update = getCurrentViewStateOrNew()
    update.discountFields.discountCode = code
    setViewState(update)
}

fun AccountViewModel.setOfferType(type: OfferType) {
    val update = getCurrentViewStateOrNew()
    update.discountFields.offerType = type
    setViewState(update)
}

fun AccountViewModel.setDiscountValuePercentage(value:String){
    val update = getCurrentViewStateOrNew()
    update.discountFields.discountValuePercentage = value
    setViewState(update)
}

fun AccountViewModel.setDiscountValueFixed(value:String){
    val update = getCurrentViewStateOrNew()
    update.discountFields.discountValueFixed = value
    setViewState(update)
}

fun AccountViewModel.clearDiscountFields(){
    val update = getCurrentViewStateOrNew()
    update.discountFields=AccountViewState.DiscountFields()
    setViewState(update)
}