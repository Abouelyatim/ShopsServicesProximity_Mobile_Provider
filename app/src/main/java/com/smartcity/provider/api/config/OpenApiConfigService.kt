package com.smartcity.provider.api.config

import androidx.lifecycle.LiveData
import com.smartcity.provider.api.GenericResponse
import com.smartcity.provider.api.auth.network_responses.StoreResponse
import com.smartcity.provider.api.main.responses.ListGenericResponse
import com.smartcity.provider.di.config.ConfigScope
import com.smartcity.provider.models.Category
import com.smartcity.provider.util.GenericApiResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

@ConfigScope
interface OpenApiConfigService {
    @Multipart
    @POST("store/create")
    fun createStore(
        @Part("store")  store: RequestBody,
        @Part image: MultipartBody.Part?
    ): LiveData<GenericApiResponse<StoreResponse>>

    @GET("store/category")
    fun getStoreCategories(
        @Query(value = "id") id : Long
    ): LiveData<GenericApiResponse<ListGenericResponse<Category>>>

    @POST("store/category")
    fun setStoreCategories(
        @Query(value = "id") id : Long,
        @Query(value = "categories") categories : List<String>
    ): LiveData<GenericApiResponse<GenericResponse>>

    @GET("category")
    fun getAllCategories(): LiveData<GenericApiResponse<ListGenericResponse<Category>>>
}