package com.smartcity.provider.ui.main.account.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.smartcity.provider.di.main.MainScope
import com.smartcity.provider.repository.main.AccountRepositoryImpl
import com.smartcity.provider.session.SessionManager
import com.smartcity.provider.ui.BaseViewModel
import com.smartcity.provider.ui.DataState
import com.smartcity.provider.ui.Loading
import com.smartcity.provider.ui.main.account.state.AccountStateEvent
import com.smartcity.provider.ui.main.account.state.AccountStateEvent.*
import com.smartcity.provider.ui.main.account.state.AccountViewState
import com.smartcity.provider.util.AbsentLiveData
import javax.inject.Inject

@MainScope
class AccountViewModel
@Inject
constructor(
    val sessionManager: SessionManager,
    val accountRepository: AccountRepositoryImpl
)
    : BaseViewModel<AccountStateEvent, AccountViewState>()
{

    override fun handleStateEvent(stateEvent: AccountStateEvent): LiveData<DataState<AccountViewState>> {
        when(stateEvent){

            is GetNotificationSettingsEvent ->{
                return accountRepository.attemptGetNotificationSettings(
                )
            }

            is SaveNotificationSettingsEvent ->{
                return accountRepository.attemptSetNotificationSettings(
                    stateEvent.settings
                )
            }

            is SavePolicyEvent ->{
                return sessionManager.cachedToken.value?.let { authToken ->
                    stateEvent.policy.providerId=authToken.account_pk!!.toLong()
                    accountRepository.attemptCreatePolicy(
                        stateEvent.policy
                    )

                }?: AbsentLiveData.create()
            }

            is SetStoreInformationEvent ->{
                return sessionManager.cachedToken.value?.let { authToken ->
                    stateEvent.storeInformation.providerId=authToken.account_pk!!.toLong()
                    accountRepository.attemptSetStoreInformation(
                        stateEvent.storeInformation
                    )

                }?: AbsentLiveData.create()
            }

            is GetStoreInformationEvent ->{
                return sessionManager.cachedToken.value?.let { authToken ->
                    accountRepository.attemptGetStoreInformation(
                        authToken.account_pk!!.toLong()
                    )
                }?: AbsentLiveData.create()
            }

            is GetCustomCategoriesEvent ->{
                return sessionManager.cachedToken.value?.let { authToken ->
                    accountRepository.attemptGetCustomCategories(
                        authToken.account_pk!!.toLong()
                    )
                }?: AbsentLiveData.create()
            }

            is GetProductsEvent ->{
                return accountRepository.attemptGetProducts(
                    stateEvent.id
                )
            }

            is CreateOfferEvent ->{
                return sessionManager.cachedToken.value?.let { authToken ->
                    stateEvent.offer.providerId=authToken.account_pk!!.toLong()
                    accountRepository.attemptCreateOffer(
                        stateEvent.offer
                    )

                }?: AbsentLiveData.create()
            }

            is GetOffersEvent ->{
                return sessionManager.cachedToken.value?.let { authToken ->
                    accountRepository.attemptGetOffers(
                        authToken.account_pk!!.toLong(),
                        getSelectedOfferFilter()?.second
                    )
                }?: AbsentLiveData.create()
            }

            is DeleteOfferEvent ->{
                return accountRepository.attemptDeleteOffer(
                    stateEvent.id
                )
            }

            is UpdateOfferEvent ->{
                return sessionManager.cachedToken.value?.let { authToken ->
                    stateEvent.offer.providerId=authToken.account_pk!!.toLong()
                    accountRepository.attemptUpdateOffer(
                        stateEvent.offer
                    )

                }?: AbsentLiveData.create()
            }

            is AllCategoriesEvent ->{
                return accountRepository.attemptAllCategory()
            }

            is CreateFlashDealEvent ->{
                return sessionManager.cachedToken.value?.let { authToken ->
                    stateEvent.flashDeal.providerId=authToken.account_pk!!.toLong()
                    accountRepository.attemptCreateFlashDeal(
                        stateEvent.flashDeal
                    )
                }?: AbsentLiveData.create()
            }

            is GetFlashDealsEvent ->{
                return sessionManager.cachedToken.value?.let { authToken ->
                    accountRepository.attemptGetFlashDeals(
                        authToken.account_pk!!.toLong()
                    )
                }?: AbsentLiveData.create()
            }

            is GetSearchFlashDealsEvent ->{
                return sessionManager.cachedToken.value?.let { authToken ->
                    accountRepository.attemptGetSearchFlashDeals(
                        authToken.account_pk!!.toLong(),
                        getSearchFlashDealRangeDate().first,
                        getSearchFlashDealRangeDate().second
                    )
                }?: AbsentLiveData.create()
            }

            is None ->{
                return liveData {
                    emit(
                        DataState<AccountViewState>(
                            null,
                            Loading(false),
                            null
                        )
                    )
                }
            }
        }
    }

    override fun initNewViewState(): AccountViewState {
        return AccountViewState()
    }

    fun cancelActiveJobs(){
        accountRepository.cancelActiveJobs() // cancel active jobs
        handlePendingData() // hide progress bar
    }

    fun handlePendingData(){
        setStateEvent(None())
    }

    override fun onCleared() {
        super.onCleared()
        cancelActiveJobs()
    }
}