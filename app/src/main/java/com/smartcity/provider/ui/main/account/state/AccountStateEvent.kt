package com.smartcity.provider.ui.main.account.state

import com.smartcity.provider.models.FlashDeal
import com.smartcity.provider.models.Offer
import com.smartcity.provider.models.Policy
import com.smartcity.provider.models.StoreInformation
import com.smartcity.provider.ui.main.custom_category.state.CustomCategoryStateEvent

sealed class AccountStateEvent {

    class SaveNotificationSettings(
        val settings:List<String>
    ):AccountStateEvent()

    class GetNotificationSettings():AccountStateEvent()

    class SavePolicy(
        var policy: Policy
    ):AccountStateEvent()

    class SetStoreInformation(
        var storeInformation: StoreInformation
    ):AccountStateEvent()

    class GetStoreInformation(
    ):AccountStateEvent()

    class AllCategoriesEvent:AccountStateEvent()


    class GetCustomCategoriesEvent : AccountStateEvent()

    class GetProductsEvent(
        val id: Long
    ) : AccountStateEvent()

    class CreateOfferEvent(
        val offer: Offer
    ) : AccountStateEvent()

    class GetOffersEvent():AccountStateEvent()

    class DeleteOfferEvent(
        val id:Long
    ):AccountStateEvent()

    class UpdateOfferEvent(
        val offer: Offer
    ) : AccountStateEvent()

    class CreateFlashDealEvent(
        val flashDeal: FlashDeal
    ): AccountStateEvent()

    class GetFlashDealsEvent():AccountStateEvent()

    class None: AccountStateEvent()
}