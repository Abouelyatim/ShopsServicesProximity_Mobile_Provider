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

    override fun handleStateEvent(stateEvent: OrderStateEvent): LiveData<DataState<OrderViewState>> {
        when(stateEvent){


            is GetOrderEvent -> {
                return sessionManager.cachedToken.value?.let { authToken ->
                    orderRepository.attemptGetOrders(
                        authToken.account_pk!!.toLong(),
                        if (getSelectedSortFilter() == null) "DESC" else (if(getSelectedSortFilter()!!.second == "date") getSelectedSortFilter()!!.third else ""),
                        if (getSelectedSortFilter() == null) "" else (if(getSelectedSortFilter()!!.second == "amount") getSelectedSortFilter()!!.third else ""),
                        getOrderStepFilter(),
                        if (getSelectedTypeFilter() == null) "" else getSelectedTypeFilter()!!.third
                    )
                }?: AbsentLiveData.create()
            }

            is GetTodayOrderEvent ->{
                return sessionManager.cachedToken.value?.let { authToken ->
                    orderRepository.attemptGetTodayOrders(
                        authToken.account_pk!!.toLong(),
                        if (getSelectedSortFilter() == null) "DESC" else (if(getSelectedSortFilter()!!.second == "date") getSelectedSortFilter()!!.third else ""),
                        if (getSelectedSortFilter() == null) "" else (if(getSelectedSortFilter()!!.second == "amount") getSelectedSortFilter()!!.third else ""),
                        getOrderStepFilter(),
                        if (getSelectedTypeFilter() == null) "" else getSelectedTypeFilter()!!.third
                    )
                }?: AbsentLiveData.create()
            }

            is GetOrderByDateEvent ->{
                return sessionManager.cachedToken.value?.let { authToken ->
                    orderRepository.attemptGetOrdersByDate(
                        authToken.account_pk!!.toLong(),
                        getRangeDate().first,
                        getRangeDate().second,
                        if (getSelectedSortFilter() == null) "DESC" else (if(getSelectedSortFilter()!!.second == "date") getSelectedSortFilter()!!.third else ""),
                        if (getSelectedSortFilter() == null) "" else (if(getSelectedSortFilter()!!.second == "amount") getSelectedSortFilter()!!.third else ""),
                        getOrderStepFilter(),
                        if (getSelectedTypeFilter() == null) "" else getSelectedTypeFilter()!!.third
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

            is GetOrderByIdEvent ->{
                return sessionManager.cachedToken.value?.let { authToken ->
                    orderRepository.attemptGetOrderById(
                        authToken.account_pk!!.toLong(),
                        stateEvent.orderId
                    )
                }?: AbsentLiveData.create()
            }

            is SearchOrderByReceiverEvent ->{
                return sessionManager.cachedToken.value?.let { authToken ->
                    orderRepository.attemptSearchOrderByReceiver(
                        authToken.account_pk!!.toLong(),
                        stateEvent.firstName,
                        stateEvent.lastName
                    )
                }?: AbsentLiveData.create()
            }

            is SearchOrderByDateEvent ->{
                return sessionManager.cachedToken.value?.let { authToken ->
                    orderRepository.attemptSearchOrderByDate(
                        authToken.account_pk!!.toLong(),
                        stateEvent.date
                    )
                }?: AbsentLiveData.create()
            }

            is SetOrderNoteEvent ->{
                return orderRepository.attemptSetOrderNote(
                    stateEvent.id,
                    stateEvent.note
                )
            }

            is GetPastOrderEvent ->{
                return sessionManager.cachedToken.value?.let { authToken ->
                    return orderRepository.attemptGetPastOrders(
                        authToken.account_pk!!.toLong()
                    )
                }?: AbsentLiveData.create()
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











