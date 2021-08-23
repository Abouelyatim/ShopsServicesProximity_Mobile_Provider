package com.smartcity.provider.api.main

import androidx.lifecycle.LiveData
import androidx.room.Update
import com.smartcity.provider.api.GenericResponse
import com.smartcity.provider.api.auth.network_responses.StoreResponse
import com.smartcity.provider.api.main.responses.*
import com.smartcity.provider.di.main.MainScope
import com.smartcity.provider.models.*
import com.smartcity.provider.models.product.OrderType
import com.smartcity.provider.models.product.Product
import com.smartcity.provider.util.GenericApiResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

@MainScope
interface OpenApiMainService {

    @GET("store/customCategory/all/{id}")
    fun getAllcustomCategory(@Path("id") id: Long?):LiveData<GenericApiResponse<ListCustomCategoryResponse>>

    @POST("store/customCategory/create")
    @FormUrlEncoded
    fun createCustomCategory(
        @Field("provider") provider:Long,
        @Field("name") name:String
    ): LiveData<GenericApiResponse<CustomCategoryResponse>>

    @DELETE("store/customCategory/delete/{id}")
    fun deleteCustomCategory(@Path("id") id: Long?):LiveData<GenericApiResponse<GenericResponse>>

    @PUT("store/customCategory/update")
    @FormUrlEncoded
    fun updateCustomCategory(
        @Field("id") id:Long,
        @Field("name") name:String,
        @Field("provider") provider:Long
    ): LiveData<GenericApiResponse<CustomCategoryResponse>>


    @Multipart
    @POST("product/create")
    fun createProduct(
        @Part("product")  product: RequestBody,
        @Part productImagesFile: List<MultipartBody.Part>,
        @Part variantesImagesFile : List<MultipartBody.Part>
    ): LiveData<GenericApiResponse<Product>>

    @Multipart
    @PUT("product/update")
    fun updateProduct(
        @Part("product")  product: RequestBody,
        @Part productImagesFile: List<MultipartBody.Part>,
        @Part variantesImagesFile : List<MultipartBody.Part>
    ): LiveData<GenericApiResponse<Product>>

    @GET("product/all/category/{id}")
    fun getAllProductByCategory(@Path("id") id: Long?):LiveData<GenericApiResponse<ListProductResponse>>

    @GET("product/all/provider/{id}")
    fun getAllProduct(@Path("id") id: Long?):LiveData<GenericApiResponse<ListProductResponse>>


    @DELETE("product/delete/{id}")
    fun deleteProduct(@Path("id") id: Long?):LiveData<GenericApiResponse<GenericResponse>>


    @GET("order/current-provider-orders")
    fun getAllOrders(
        @Query("id") id: Long?,
        @Query("date") date: String?,
        @Query("amount") amount: String?,
        @Query("step") step: OrderStep?,
        @Query("type") type: String?
    ):LiveData<GenericApiResponse<ListOrderResponse>>

    @GET("order/current-provider-today-orders")
    fun getTodayOrders(
        @Query("id") id: Long?,
        @Query("date") date: String?,
        @Query("amount") amount: String?,
        @Query("step") step: OrderStep?,
        @Query("type") type: String?
    ):LiveData<GenericApiResponse<ListOrderResponse>>

    @GET("order/current-provider-dates-orders")
    fun getOrdersByDate(
        @Query("id") id: Long?,
        @Query("startDate") startDate: String?,
        @Query("endDate") endDate: String?,
        @Query("date") date: String?,
        @Query("amount") amount: String?,
        @Query("step") step: OrderStep?,
        @Query("type") type: String?
    ):LiveData<GenericApiResponse<ListOrderResponse>>

    @POST("policy/create")
    fun createPolicy(
        @Body policy: Policy
    ): LiveData<GenericApiResponse<GenericResponse>>

    @POST("store/Information")
    fun setStoreInformation(
        @Body storeInformation: StoreInformation
    ): LiveData<GenericApiResponse<GenericResponse>>

    @GET("store/Information/{id}")
    fun getStoreInformation(
        @Path(value = "id") id:Long
    ): LiveData<GenericApiResponse<StoreInformation>>

    @PUT("order/current-store/{id}/accept")
    fun setOrderAccepted(
        @Path(value = "id") id:Long
    ): LiveData<GenericApiResponse<GenericResponse>>

    @PUT("order/current-store/{id}/reject")
    fun setOrderRejected(
        @Path(value = "id") id:Long
    ): LiveData<GenericApiResponse<GenericResponse>>

    @PUT("order/current-store/{id}/ready")
    fun setOrderReady(
        @Path(value = "id") id:Long
    ): LiveData<GenericApiResponse<GenericResponse>>

    @PUT("order/current-store/{id}/delivered")
    fun setOrderDelivered(
        @Path(value = "id") id:Long,
        @Query("comment") comment:String?,
        @Query("date") date: String?
    ): LiveData<GenericApiResponse<GenericResponse>>

    @PUT("order/current-store/{id}/pickedUp")
    fun setOrderPickedUp(
        @Path(value = "id") id:Long,
        @Query("comment") comment:String?,
        @Query("date") date: String?
    ): LiveData<GenericApiResponse<GenericResponse>>

    @POST("offer/create")
    fun createOffer(
        @Body offer:Offer
    ): LiveData<GenericApiResponse<GenericResponse>>

    @PUT("offer/update")
    fun updateOffer(
        @Body offer:Offer
    ): LiveData<GenericApiResponse<GenericResponse>>

    @GET("offer/current-provider-offers")
    fun getAllOffers(
        @Query("id") id: Long?,
        @Query("status") status: OfferState?
    ):LiveData<GenericApiResponse<ListGenericResponse<Offer>>>

    @DELETE("offer/delete/{id}")
    fun deleteOffer(
        @Path("id") id: Long?
    ):LiveData<GenericApiResponse<GenericResponse>>

    @GET("category")
    fun getAllCategory(): LiveData<GenericApiResponse<ListGenericResponse<Category>>>

    @POST("flashDeal/create")
    fun createFlashDeal(
        @Body flashDeal:FlashDeal
    ): LiveData<GenericApiResponse<GenericResponse>>

    @GET("flashDeal/current-provider-flash/{id}")
    fun getFlashDeals(
        @Path("id") id: Long?
    ): LiveData<GenericApiResponse<ListGenericResponse<FlashDeal>>>

    @GET("flashDeal/current-provider-search-flash")
    fun getSearchFlashDeals(
        @Query("id") id: Long?,
        @Query("startDate") startDate: String?,
        @Query("endDate") endDate: String?
    ): LiveData<GenericApiResponse<ListGenericResponse<FlashDeal>>>

    @GET("order/current-provider-search-orders-id")
    fun getOrderById(
        @Query("id") id: Long?,
        @Query("orderId") orderId: Long?
    ):LiveData<GenericApiResponse<ListOrderResponse>>

    @GET("order/current-provider-search-orders-receiver")
    fun searchOrderByReceiver(
        @Query("id") id: Long?,
        @Query("firstName") firstName: String?,
        @Query("lastName") lastName: String?
    ):LiveData<GenericApiResponse<ListOrderResponse>>

    @GET("order/current-provider-search-orders-date")
    fun searchOrderByDate(
        @Query("id") id: Long?,
        @Query("date") date: String?
    ):LiveData<GenericApiResponse<ListOrderResponse>>

    @PUT("order/current-store-note")
    fun setOrderNote(
        @Query("id") id: Long?,
        @Query("note") note: String
    ):LiveData<GenericApiResponse<GenericResponse>>

    @GET("order/current-provider-past-orders")
    fun getPastOrders(
        @Query("id") id: Long?
    ):LiveData<GenericApiResponse<ListOrderResponse>>
}









