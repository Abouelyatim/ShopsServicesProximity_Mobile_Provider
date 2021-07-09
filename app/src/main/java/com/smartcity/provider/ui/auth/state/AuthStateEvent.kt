package com.smartcity.provider.ui.auth.state

import com.smartcity.provider.models.Store
import com.smartcity.provider.models.StoreAddress
import okhttp3.MultipartBody
import okhttp3.RequestBody

sealed class AuthStateEvent{

    data class LoginAttemptEvent(
        val email: String,
        val password: String
    ): AuthStateEvent()

    data class RegisterAttemptEvent(
        val email: String,
        val username: String,
        val password: String,
        val confirm_password: String
    ): AuthStateEvent()

    class CheckPreviousAuthEvent(): AuthStateEvent()

    data class CreateStoreAttemptEvent(
        val store: Store,
        val image: MultipartBody.Part
    ): AuthStateEvent()

    class GetCategoryStore(): AuthStateEvent()

    class None: AuthStateEvent()
}