package com.smartcity.provider.ui.main.store.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.smartcity.provider.di.main.MainScope
import com.smartcity.provider.models.CustomCategory
import com.smartcity.provider.models.product.Product
import com.smartcity.provider.repository.main.StoreRepositoryImpl
import com.smartcity.provider.session.SessionManager
import com.smartcity.provider.ui.BaseViewModel
import com.smartcity.provider.ui.DataState
import com.smartcity.provider.ui.Loading
import com.smartcity.provider.ui.main.store.state.StoreStateEvent
import com.smartcity.provider.ui.main.store.state.StoreStateEvent.*
import com.smartcity.provider.ui.main.store.state.StoreViewState
import com.smartcity.provider.util.AbsentLiveData
import javax.inject.Inject

@MainScope
class StoreViewModel
@Inject
constructor(
    val sessionManager: SessionManager,
    val storeRepository: StoreRepositoryImpl
)
    : BaseViewModel<StoreStateEvent, StoreViewState>()
{
    override fun handleStateEvent(stateEvent: StoreStateEvent): LiveData<DataState<StoreViewState>> {
        when(stateEvent){

            is CustomCategoryMainEvent ->{
                return sessionManager.cachedToken.value?.let { authToken ->
                    storeRepository.attemptCustomCategoryMain(
                        authToken.account_pk!!.toLong()
                    )
                }?: AbsentLiveData.create()

            }

            is ProductMainEvent ->{
                return storeRepository.attemptProductMain(
                    stateEvent.id
                )
            }

            is AllProductEvent ->{
                return sessionManager.cachedToken.value?.let { authToken ->
                    storeRepository.attemptAllProduct(
                        authToken.account_pk!!.toLong()
                    )
                } ?: AbsentLiveData.create()

            }
            is None ->{
                return liveData {
                    emit(
                        DataState<StoreViewState>(
                            null,
                            Loading(false),
                            null
                        )
                    )
                }
            }
        }
    }



    override fun initNewViewState(): StoreViewState {
        return StoreViewState()
    }





    fun cancelActiveJobs(){
        storeRepository.cancelActiveJobs() // cancel active jobs
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














