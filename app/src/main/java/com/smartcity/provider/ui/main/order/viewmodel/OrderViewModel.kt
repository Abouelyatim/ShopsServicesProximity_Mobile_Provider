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
import com.smartcity.provider.util.PreferenceKeys.Companion.ORDER_AMOUNT_FILTER
import com.smartcity.provider.util.PreferenceKeys.Companion.ORDER_DATE_FILTER
import javax.inject.Inject

@MainScope
class OrderViewModel
@Inject
constructor(
    private val sessionManager: SessionManager,
    private val orderRepository: OrderRepository,
    private val sharedPreferences: SharedPreferences,
    private val editor: SharedPreferences.Editor
): BaseViewModel<OrderStateEvent, OrderViewState>(){

    init {
        setDateFilter(
            sharedPreferences.getString(
                ORDER_DATE_FILTER,
                "DESC"
            )
        )
        setAmountFilter(
            sharedPreferences.getString(
                ORDER_AMOUNT_FILTER,
                "ASC"
            )
        )
    }

    fun saveFilterOptions(dateFilter: String, amountFilter: String){
        editor.putString(ORDER_DATE_FILTER, dateFilter)
        editor.apply()

        editor.putString(ORDER_AMOUNT_FILTER, amountFilter)
        editor.apply()
    }

    override fun handleStateEvent(stateEvent: OrderStateEvent): LiveData<DataState<OrderViewState>> {
        when(stateEvent){


            is GetOrderEvent -> {
                return sessionManager.cachedToken.value?.let { authToken ->
                    orderRepository.attemptGetOrders(
                        authToken.account_pk!!.toLong(),
                        getDateFilter(),
                        getAmountFilter(),
                        getOrderStepFilter()
                    )
                }?: AbsentLiveData.create()
            }

            is GetTodayOrderEvent ->{
                return sessionManager.cachedToken.value?.let { authToken ->
                    orderRepository.attemptGetTodayOrders(
                        authToken.account_pk!!.toLong(),
                        getDateFilter(),
                        getAmountFilter(),
                        getOrderStepFilter()
                    )
                }?: AbsentLiveData.create()
            }

            is GetOrderByDateEvent ->{
                return sessionManager.cachedToken.value?.let { authToken ->
                    orderRepository.attemptGetOrdersByDate(
                        authToken.account_pk!!.toLong(),
                        getRangeDate().first,
                        getRangeDate().second,
                        getDateFilter(),
                        getAmountFilter(),
                        getOrderStepFilter()
                    )
                }?: AbsentLiveData.create()
            }

            is SetOrderAcceptedEvent ->{
                return orderRepository.attemptSetOrderAccepted(
                    stateEvent.id
                )
            }

            is SetOrderRejectedEvent ->{
                return orderRepository.attemptSetOrderRejected(
                    stateEvent.id
                )
            }

            is SetOrderReadyEvent ->{
                return orderRepository.attemptSetOrderReady(
                    stateEvent.id
                )
            }

            is SetOrderDeliveredEvent ->{
                return orderRepository.attemptSetOrderDelivered(
                    stateEvent.id,
                    stateEvent.comment,
                    stateEvent.date
                )
            }

            is SetOrderPickedUpEvent ->{
                return orderRepository.attemptSetOrderPickedUp(
                    stateEvent.id,
                    stateEvent.comment,
                    stateEvent.date
                )
            }

            is None ->{
                return liveData {
                    emit(
                        DataState<OrderViewState>(
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











