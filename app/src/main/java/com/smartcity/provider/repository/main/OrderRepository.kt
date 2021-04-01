package com.smartcity.provider.repository.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.switchMap
import com.smartcity.provider.api.GenericResponse
import com.smartcity.provider.api.main.OpenApiMainService
import com.smartcity.provider.api.main.responses.BlogCreateUpdateResponse
import com.smartcity.provider.api.main.responses.BlogListSearchResponse
import com.smartcity.provider.api.main.responses.ListOrderResponse
import com.smartcity.provider.di.main.MainScope
import com.smartcity.provider.models.AuthToken
import com.smartcity.provider.models.BlogPost
import com.smartcity.provider.models.product.Order
import com.smartcity.provider.persistence.BlogPostDao
import com.smartcity.provider.persistence.returnOrderedBlogQuery
import com.smartcity.provider.repository.JobManager
import com.smartcity.provider.repository.NetworkBoundResource
import com.smartcity.provider.session.SessionManager
import com.smartcity.provider.ui.DataState
import com.smartcity.provider.ui.Response
import com.smartcity.provider.ui.ResponseType
import com.smartcity.provider.ui.main.order.state.OrderViewState
import com.smartcity.provider.ui.main.order.state.OrderViewState.*
import com.smartcity.provider.util.*
import com.smartcity.provider.util.Constants.Companion.PAGINATION_PAGE_SIZE
import com.smartcity.provider.util.ErrorHandling.Companion.ERROR_UNKNOWN
import com.smartcity.provider.util.SuccessHandling.Companion.RESPONSE_HAS_PERMISSION_TO_EDIT
import com.smartcity.provider.util.SuccessHandling.Companion.RESPONSE_NO_PERMISSION_TO_EDIT
import com.smartcity.provider.util.SuccessHandling.Companion.SUCCESS_BLOG_DELETED
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import okhttp3.MultipartBody
import okhttp3.RequestBody
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
        id:Long
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
                    id= id
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


}
















