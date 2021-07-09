package com.smartcity.provider.api.auth

import androidx.lifecycle.LiveData
import com.smartcity.provider.api.auth.network_responses.CategoryStoreResponse
import com.smartcity.provider.util.GenericApiResponse
import com.smartcity.provider.api.auth.network_responses.LoginResponse
import com.smartcity.provider.api.auth.network_responses.RegistrationResponse
import com.smartcity.provider.api.auth.network_responses.StoreResponse
import com.smartcity.provider.di.auth.AuthScope
import com.smartcity.provider.models.StoreAddress
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

@AuthScope
interface OpenApiAuthService {

    @POST("provider/login")
    @FormUrlEncoded
    fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): LiveData<GenericApiResponse<LoginResponse>>

    @POST("provider/register")
    @FormUrlEncoded
    fun register(
        @Field("email") email: String,
        @Field("userName") username: String,
        @Field("passWord") password: String,
        @Field("passWord2") password2: String
    ): LiveData<GenericApiResponse<RegistrationResponse>>

    @Multipart
    @POST("store/create")
    fun createStore(
        @Part("store")  store: RequestBody,
        @Part image: MultipartBody.Part?
    ): LiveData<GenericApiResponse<StoreResponse>>

    @GET("store/category/all")
    fun getCategoryStore(
    ): LiveData<GenericApiResponse<CategoryStoreResponse>>

}
