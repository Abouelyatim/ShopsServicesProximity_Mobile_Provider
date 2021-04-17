package com.smartcity.provider.repository.main

import android.util.Log
import androidx.lifecycle.LiveData
import com.smartcity.provider.api.main.OpenApiMainService
import com.smartcity.provider.api.main.responses.ListOrderResponse
import com.smartcity.provider.di.main.MainScope
import com.smartcity.provider.models.product.Order
import com.smartcity.provider.repository.JobManager
import com.smartcity.provider.repository.NetworkBoundResource
import com.smartcity.provider.session.SessionManager
import com.smartcity.provider.ui.DataState
import com.smartcity.provider.ui.Response
import com.smartcity.provider.ui.ResponseType
import com.smartcity.provider.ui.main.custom_category.state.CustomCategoryViewState
import com.smartcity.provider.ui.main.order.state.OrderViewState
import com.smartcity.provider.ui.main.order.state.OrderViewState.OrderFields
import com.smartcity.provider.util.AbsentLiveData
import com.smartcity.provider.util.ApiSuccessResponse
import com.smartcity.provider.util.ErrorHandling.Companion.ERROR_MUST_SELECT_TWO_DATES
import com.smartcity.provider.util.GenericApiResponse
import com.smartcity.provider.util.SuccessHandling
import kotlinx.coroutines.Job
import javax.inject.Inject

@MainScope
class OrderRepository
@Inject
constructor(
    val openApiMainService: OpenApiMainService,
    val sessionManager: SessionManager
): JobManager("BlogRepository")
{
    private val TAG: String = "AppDebug"

    fun attemptGetOrders(
        id:Long,
        dateFilter:String,
        amountFilter:String
    ): LiveData<DataState<OrderViewState>> {
        return object: NetworkBoundResource<ListOrderResponse, Order, OrderViewState>(
            sessionManager.isConnectedToTheInternet(),
            true,
            true,
            false
        ){


            // not applicable
            override suspend fun createCacheRequestAndReturn() {

            }

            override suspend fun handleApiSuccessResponse(response: ApiSuccessResponse<ListOrderResponse>) {
                Log.d(TAG, "handleApiSuccessResponse: ${response}")

                onCompleteJob(
                    DataState.data(
                        data = OrderViewState(
                            orderFields = OrderFields(
                                orderList = response.body.results
                            )
                        ),
                        response = Response(
                            SuccessHandling.DONE_Orders,
                            ResponseType.None()
                        )
                    )
                )
            }

            // not applicable
            override fun loadFromCache(): LiveData<OrderViewState> {
                return AbsentLiveData.create()
            }

            override fun createCall(): LiveData<GenericApiResponse<ListOrderResponse>> {
                return openApiMainService.getAllOrders(
                    id= id,
                    date = dateFilter,
                    amount = amountFilter
                )
            }

            // not applicable
            override suspend fun updateLocalDb(cacheObject: Order?) {
            }

            override fun setJob(job: Job) {
                addJob("attemptGetOrders", job)
            }


        }.asLiveData()
    }


    fun attemptGetTodayOrders(
        id:Long,
        dateFilter:String,
        amountFilter:String
    ): LiveData<DataState<OrderViewState>> {
        return object: NetworkBoundResource<ListOrderResponse, Order, OrderViewState>(
            sessionManager.isConnectedToTheInternet(),
            true,
            true,
            false
        ){


            // not applicable
            override suspend fun createCacheRequestAndReturn() {

            }

            override suspend fun handleApiSuccessResponse(response: ApiSuccessResponse<ListOrderResponse>) {
                Log.d(TAG, "handleApiSuccessResponse: ${response}")

                onCompleteJob(
                    DataState.data(
                        data = OrderViewState(
                            orderFields = OrderFields(
                                orderList = response.body.results
                            )
                        ),
                        response = Response(
                            SuccessHandling.DONE_Orders,
                            ResponseType.None()
                        )
                    )
                )
            }

            // not applicable
            override fun loadFromCache(): LiveData<OrderViewState> {
                return AbsentLiveData.create()
            }

            override fun createCall(): LiveData<GenericApiResponse<ListOrderResponse>> {
                return openApiMainService.getTodayOrders(
                    id= id,
                    date = dateFilter,
                    amount = amountFilter
                )
            }

            // not applicable
            override suspend fun updateLocalDb(cacheObject: Order?) {
            }

            override fun setJob(job: Job) {
                addJob("attemptGetTodayOrders", job)
            }


        }.asLiveData()
    }


    fun attemptGetOrdersByDate(
        id:Long,
        startDate: String?,
        endDate: String?,
        dateFilter:String,
        amountFilter:String
    ): LiveData<DataState<OrderViewState>> {

        if(startDate.isNullOrEmpty() || endDate.isNullOrEmpty()){
            return returnErrorResponse(ERROR_MUST_SELECT_TWO_DATES, ResponseType.Dialog())
        }


        return object: NetworkBoundResource<ListOrderResponse, Order, OrderViewState>(
            sessionManager.isConnectedToTheInternet(),
            true,
            true,
            false
        ){


            // not applicable
            override suspend fun createCacheRequestAndReturn() {

            }

            override suspend fun handleApiSuccessResponse(response: ApiSuccessResponse<ListOrderResponse>) {
                Log.d(TAG, "handleApiSuccessResponse: ${response}")

                onCompleteJob(
                    DataState.data(
                        data = OrderViewState(
                            orderFields = OrderFields(
                                orderList = response.body.results
                            )
                        ),
                        response = Response(
                            SuccessHandling.DONE_Orders,
                            ResponseType.None()
                        )
                    )
                )
            }

            // not applicable
            override fun loadFromCache(): LiveData<OrderViewState> {
                return AbsentLiveData.create()
            }

            override fun createCall(): LiveData<GenericApiResponse<ListOrderResponse>> {
                return openApiMainService.getOrdersByDate(
                    id= id,
                    startDate = startDate,
                    endDate = endDate,
                    date = dateFilter,
                    amount = amountFilter
                )
            }

            // not applicable
            override suspend fun updateLocalDb(cacheObject: Order?) {
            }

            override fun setJob(job: Job) {
                addJob("attemptGetOrdersByDate", job)
            }


        }.asLiveData()
    }


    private fun returnErrorResponse(errorMessage: String, responseType: ResponseType): LiveData<DataState<OrderViewState>>{
        Log.d(TAG, "returnErrorResponse: ${errorMessage}")

        return object: LiveData<DataState<OrderViewState>>(){
            override fun onActive() {
                super.onActive()
                value = DataState.error(
                    Response(
                        errorMessage,
                        responseType
                    )
                )
            }
        }
    }

}
















