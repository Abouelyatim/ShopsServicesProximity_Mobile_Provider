package com.smartcity.provider.ui.config.state

import com.smartcity.provider.models.Store
import okhttp3.MultipartBody

sealed class ConfigStateEvent {

    data class CreateStoreAttemptEvent(
        val store: Store,
        val image: MultipartBody.Part
    ): ConfigStateEvent()

    class GetStoreCategoriesEvent(): ConfigStateEvent()

    class SetStoreCategoriesEvent(
        var categories : List<String>
    ): ConfigStateEvent()

    class GetAllCategoriesEvent(): ConfigStateEvent()

    class None: ConfigStateEvent()
}