package com.smartcity.provider.ui.main.account.viewmodel

import com.smartcity.provider.models.SelfPickUpOptions
import com.smartcity.provider.models.TaxRange
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