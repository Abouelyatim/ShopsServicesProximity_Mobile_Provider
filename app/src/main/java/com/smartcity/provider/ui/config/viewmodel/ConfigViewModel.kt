package com.smartcity.provider.ui.config.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.google.gson.Gson
import com.smartcity.provider.di.config.ConfigScope
import com.smartcity.provider.repository.config.ConfigRepositoryImpl
import com.smartcity.provider.session.SessionManager
import com.smartcity.provider.ui.BaseViewModel
import com.smartcity.provider.ui.DataState
import com.smartcity.provider.ui.Loading
import com.smartcity.provider.ui.config.state.ConfigStateEvent
import com.smartcity.provider.ui.config.state.ConfigViewState
import com.smartcity.provider.util.AbsentLiveData
import okhttp3.MediaType
import okhttp3.RequestBody
import javax.inject.Inject

@ConfigScope
class ConfigViewModel
@Inject
constructor(
    val configRepository: ConfigRepositoryImpl,
    private val sessionManager: SessionManager
): BaseViewModel<ConfigStateEvent, ConfigViewState>()
{
    override fun handleStateEvent(stateEvent: ConfigStateEvent): LiveData<DataState<ConfigViewState>> {
        when(stateEvent){

            is ConfigStateEvent.CreateStoreAttemptEvent ->{
                return sessionManager.cachedToken.value?.let { authToken ->
                    stateEvent.store.provider=authToken.account_pk!!.toLong()
                    val gson = Gson()
                    val productJson: String = gson.toJson(stateEvent.store)
                    val requestBody= RequestBody.create(
                        MediaType.parse("application/json"),
                        productJson
                    )
                    configRepository.attemptCreateStore(
                        requestBody,
                        stateEvent.image
                    )
                }?: AbsentLiveData.create()

            }


            is ConfigStateEvent.GetStoreCategoriesEvent ->{
                return sessionManager.cachedToken.value?.let { authToken ->
                    return configRepository.attemptGetStoreCategories(
                        authToken.account_pk!!.toLong()
                    )
                }?: AbsentLiveData.create()
            }

            is ConfigStateEvent.SetStoreCategoriesEvent ->{
                return sessionManager.cachedToken.value?.let { authToken ->
                    return configRepository.attemptSetStoreCategories(
                        authToken.account_pk!!.toLong(),
                        stateEvent.categories
                    )
                }?: AbsentLiveData.create()

            }

            is ConfigStateEvent.GetAllCategoriesEvent ->{
                return configRepository.attemptGetAllCategories()
            }

            is ConfigStateEvent.None ->{
                return liveData {
                    emit(
                        DataState<ConfigViewState>(
                            null,
                            Loading(false),
                            null
                        )
                    )
                }
            }
        }
    }

    override fun initNewViewState(): ConfigViewState {
        return ConfigViewState()
    }

    fun cancelActiveJobs(){
        handlePendingData()
        configRepository.cancelActiveJobs()
    }

    fun handlePendingData(){
        setStateEvent(ConfigStateEvent.None())
    }

    override fun onCleared() {
        super.onCleared()
        cancelActiveJobs()
    }
}