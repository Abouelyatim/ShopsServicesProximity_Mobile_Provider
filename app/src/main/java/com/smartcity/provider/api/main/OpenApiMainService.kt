package com.smartcity.provider.api.main

import androidx.lifecycle.LiveData
import androidx.room.Update
import com.smartcity.provider.api.GenericResponse
import com.smartcity.provider.api.auth.network_responses.StoreResponse
import com.smartcity.provider.api.main.responses.*
import com.smartcity.provider.di.main.MainScope
import com.smartcity.provider.models.AccountProperties
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









    @GET("account/properties")
    fun getAccountProperties(
        @Header("Authorization") authorization: String
    ): LiveData<GenericApiResponse<AccountProperties>>

    @PUT("account/properties/update")
    @FormUrlEncoded
    fun saveAccountProperties(
        @Header("Authorization") authorization: String,
        @Field("email") email: String,
        @Field("username") username: String
    ): LiveData<GenericApiResponse<GenericResponse>>

    @PUT("account/change_password/")
    @FormUrlEncoded
    fun updatePassword(
        @Header("Authorization") authorization: String,
        @Field("old_password") currentPassword: String,
        @Field("new_password") newPassword: String,
        @Field("confirm_new_password") confirmNewPassword: String
    ): LiveData<GenericApiResponse<GenericResponse>>

    @GET("blog/list")
    fun searchListBlogPosts(
        @Header("Authorization") authorization: String,
        @Query("search") query: String,
        @Query("ordering") ordering: String,
        @Query("page") page: Int
    ): LiveData<GenericApiResponse<BlogListSearchResponse>>


    @GET("blog/{slug}/is_author")
    fun isAuthorOfBlogPost(
        @Header("Authorization") authorization: String,
        @Path("slug") slug: String
    ): LiveData<GenericApiResponse<GenericResponse>>


    @DELETE("blog/{slug}/delete")
    fun deleteBlogPost(
        @Header("Authorization") authorization: String,
        @Path("slug") slug: String
    ): LiveData<GenericApiResponse<GenericResponse>>

    @Multipart
    @PUT("blog/{slug}/update")
    fun updateBlog(
        @Header("Authorization") authorization: String,
        @Path("slug") slug: String,
        @Part("title") title: RequestBody,
        @Part("body") body: RequestBody,
        @Part image: MultipartBody.Part?
    ): LiveData<GenericApiResponse<BlogCreateUpdateResponse>>


    @Multipart
    @POST("blog/create")
    fun createBlog(
        @Header("Authorization") authorization: String,
        @Part("title") title: RequestBody,
        @Part("body") body: RequestBody,
        @Part image: MultipartBody.Part?
    ): LiveData<GenericApiResponse<BlogCreateUpdateResponse>>
}









