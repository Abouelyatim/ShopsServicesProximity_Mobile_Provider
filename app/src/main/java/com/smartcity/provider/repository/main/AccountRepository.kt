package com.smartcity.provider.repository.main

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import com.smartcity.provider.api.GenericResponse
import com.smartcity.provider.api.main.OpenApiMainService
import com.smartcity.provider.di.main.MainScope
import com.smartcity.provider.models.StoreInformation
import com.smartcity.provider.repository.JobManager
import com.smartcity.provider.repository.NetworkBoundResource
import com.smartcity.provider.session.SessionManager
import com.smartcity.provider.ui.DataState
import com.smartcity.provider.ui.Response
import com.smartcity.provider.ui.ResponseType
import com.smartcity.provider.ui.main.account.state.AccountViewState
import com.smartcity.provider.util.*
import com.smartcity.provider.util.SuccessHandling.Companion.RESPONSE_GET_NOTIFICATION_SETTINGS_DONE
import com.smartcity.provider.util.SuccessHandling.Companion.RESPONSE_SAVE_NOTIFICATION_SETTINGS_DONE
import kotlinx.coroutines.Job
import javax.inject.Inject

@MainScope
class AccountRepository
@Inject
constructor(
    val openApiMainService: OpenApiMainService,
    val sessionManager: SessionManager,
    val sharedPreferences: SharedPreferences,
    val sharedPrefsEditor: SharedPreferences.Editor
): JobManager("AccountRepository")
{
    private val TAG: String = "AppDebug"

    fun attemptSetNotificationSettings(
        settings: List<String>
    ): LiveData<DataState<AccountViewState>> {
        Log.d(TAG, "attemptSetNotificationSettings")
        saveNotificationSettings(settings)
        return returnSettingsDone(null,Response(RESPONSE_SAVE_NOTIFICATION_SETTINGS_DONE, ResponseType.Toast()))
    }

    fun attemptGetNotificationSettings(
    ): LiveData<DataState<AccountViewState>>{
        val settings = sharedPreferences.getStringSet(PreferenceKeys.NOTIFICATION_SETTINGS, null)
        settings?.let {
            return returnSettingsDone(
                AccountViewState(notificationSettings = it.toList()),
                Response(RESPONSE_GET_NOTIFICATION_SETTINGS_DONE, ResponseType.None())
            )
        }
        return returnSettingsDone(
            AccountViewState(notificationSettings = listOf()),
            Response(RESPONSE_GET_NOTIFICATION_SETTINGS_DONE, ResponseType.None())
        )
    }

    private fun saveNotificationSettings(settings: List<String>){
        sharedPrefsEditor.putStringSet(PreferenceKeys.NOTIFICATION_SETTINGS,settings.toMutableSet())
        sharedPrefsEditor.apply()
    }

    private fun returnSettingsDone(data:AccountViewState?,response: Response?): LiveData<DataState<AccountViewState>>{
        return object: LiveData<DataState<AccountViewState>>(){
            override fun onActive() {
                super.onActive()
                value = DataState.data(data, response)
            }
        }
    }

    fun attemptCreatePolicy(
        policy: com.smartcity.provider.models.Policy
    ): LiveData<DataState<AccountViewState>> {


        return object :
            NetworkBoundResource<GenericResponse, Any, AccountViewState>(
                sessionManager.isConnectedToTheInternet(),
                true,
                true,
                false
            ) {
            // Ignore
            override suspend fun createCacheRequestAndReturn() {

            }

            override suspend fun handleApiSuccessResponse(response: ApiSuccessResponse<GenericResponse>) {
                Log.d(TAG, "handleApiSuccessResponse: ${response}")

                onCompleteJob(
                    DataState.data(
                        data = null
                        ,
                        response = Response(
                            SuccessHandling.CREATION_DONE,
                            ResponseType.Toast()
                        )
                    )
                )
            }

            override fun createCall(): LiveData<GenericApiResponse<GenericResponse>> {

                return openApiMainService.createPolicy(
                    policy = policy
                )
            }

            override fun loadFromCache(): LiveData<AccountViewState> {
                return AbsentLiveData.create()
            }



            override fun setJob(job: Job) {
                addJob("attemptCreatePolicy", job)
            }

            override suspend fun updateLocalDb(cacheObject: Any?) {

            }

        }.asLiveData()
    }

    fun attemptSetStoreInformation(
        storeInformation: StoreInformation
    ): LiveData<DataState<AccountViewState>> {


        return object :
            NetworkBoundResource<GenericResponse, Any, AccountViewState>(
                sessionManager.isConnectedToTheInternet(),
                true,
                true,
                false
            ) {
            // Ignore
            override suspend fun createCacheRequestAndReturn() {

            }

            override suspend fun handleApiSuccessResponse(response: ApiSuccessResponse<GenericResponse>) {
                Log.d(TAG, "handleApiSuccessResponse: ${response}")

                onCompleteJob(
                    DataState.data(
                        data = null
                        ,
                        response = Response(
                            SuccessHandling.CREATION_DONE,
                            ResponseType.Toast()
                        )
                    )
                )
            }

            override fun createCall(): LiveData<GenericApiResponse<GenericResponse>> {

                return openApiMainService.setStoreInformation(
                    storeInformation = storeInformation
                )
            }

            override fun loadFromCache(): LiveData<AccountViewState> {
                return AbsentLiveData.create()
            }



            override fun setJob(job: Job) {
                addJob("attemptSetStoreInformation", job)
            }

            override suspend fun updateLocalDb(cacheObject: Any?) {

            }

        }.asLiveData()
    }

    fun attemptGetStoreInformation(
        id:Long
    ): LiveData<DataState<AccountViewState>> {


        return object :
            NetworkBoundResource<StoreInformation, Any, AccountViewState>(
                sessionManager.isConnectedToTheInternet(),
                true,
                true,
                false
            ) {
            // Ignore
            override suspend fun createCacheRequestAndReturn() {

            }

            override suspend fun handleApiSuccessResponse(response: ApiSuccessResponse<StoreInformation>) {
                Log.d(TAG, "handleApiSuccessResponse: ${response}")

                onCompleteJob(
                    DataState.data(
                        data = AccountViewState(
                            storeInformation=response.body
                        )
                        ,
                        response = Response(
                            SuccessHandling.DONE_STORE_INFORMATION,
                            ResponseType.None()
                        )
                    )
                )
            }

            override fun createCall(): LiveData<GenericApiResponse<StoreInformation>> {

                return openApiMainService.getStoreInformation(
                    id = id
                )
            }

            override fun loadFromCache(): LiveData<AccountViewState> {
                return AbsentLiveData.create()
            }



            override fun setJob(job: Job) {
                addJob("attemptGetStoreInformation", job)
            }

            override suspend fun updateLocalDb(cacheObject: Any?) {

            }

        }.asLiveData()
    }
}