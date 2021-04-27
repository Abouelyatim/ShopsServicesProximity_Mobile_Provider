package com.smartcity.provider.ui.main.account.viewmodel

import com.smartcity.provider.models.SelfPickUpOptions
import com.smartcity.provider.models.TaxRange

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