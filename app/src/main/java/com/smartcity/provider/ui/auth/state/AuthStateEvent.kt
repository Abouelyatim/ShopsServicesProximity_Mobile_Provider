package com.smartcity.provider.ui.auth.state

import okhttp3.MultipartBody

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
        val name: String,
        val description: String,
        val address: String,
        val category:List<String>,
        val image: MultipartBody.Part
    ): AuthStateEvent()

    class GetCategoryStore(): AuthStateEvent()

    class None: AuthStateEvent()
}