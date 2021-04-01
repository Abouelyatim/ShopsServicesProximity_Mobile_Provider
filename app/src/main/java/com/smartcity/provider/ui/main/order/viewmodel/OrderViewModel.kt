package com.smartcity.provider.ui.main.order.viewmodel

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.smartcity.provider.di.main.MainScope
import com.smartcity.provider.repository.main.OrderRepository
import com.smartcity.provider.session.SessionManager
import com.smartcity.provider.ui.BaseViewModel
import com.smartcity.provider.ui.DataState
import com.smartcity.provider.ui.Loading
import com.smartcity.provider.ui.main.order.state.OrderStateEvent
import com.smartcity.provider.ui.main.order.state.OrderStateEvent.*
import com.smartcity.provider.ui.main.order.state.OrderViewState
import com.smartcity.provider.util.AbsentLiveData
import com.smartcity.provider.util.PreferenceKeys.Companion.BLOG_FILTER
import com.smartcity.provider.util.PreferenceKeys.Companion.BLOG_ORDER
import javax.inject.Inject

@MainScope
class OrderViewModel
@Inject
constructor(
    private val sessionManager: SessionManager,
    private val orderRepository: OrderRepository
): BaseViewModel<OrderStateEvent, OrderViewState>(){


    override fun handleStateEvent(stateEvent: OrderStateEvent): LiveData<DataState<OrderViewState>> {
        when(stateEvent){


            is GetOrderEvent -> {
                return sessionManager.cachedToken.value?.let { authToken ->
                    orderRepository.attemptGetOrders(
                        authToken.account_pk!!.toLong()
                    )
                }?: AbsentLiveData.create()
            }

            is None ->{
                return liveData {
                    emit(
                        DataState(
                            null,
                            Loading(false),
                            null
                        )
                    )
                }
            }
        }
    }

    override fun initNewViewState(): OrderViewState {
        return OrderViewState()
    }



    fun cancelActiveJobs(){
        orderRepository.cancelActiveJobs() // cancel active jobs
        handlePendingData() // hide progress bar
    }

    fun handlePendingData(){
        setStateEvent(None())
    }

    override fun onCleared() {
        super.onCleared()
        cancelActiveJobs()
        Log.d(TAG, "CLEARED...")
    }


}











