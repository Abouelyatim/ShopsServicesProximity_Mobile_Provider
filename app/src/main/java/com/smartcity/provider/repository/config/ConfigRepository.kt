package com.smartcity.provider.repository.config

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import com.smartcity.provider.api.GenericResponse
import com.smartcity.provider.api.auth.network_responses.StoreResponse
import com.smartcity.provider.api.config.OpenApiConfigService
import com.smartcity.provider.api.main.responses.ListGenericResponse
import com.smartcity.provider.di.config.ConfigScope
import com.smartcity.provider.models.Category
import com.smartcity.provider.repository.JobManager
import com.smartcity.provider.repository.NetworkBoundResource
import com.smartcity.provider.session.SessionManager
import com.smartcity.provider.ui.DataState
import com.smartcity.provider.ui.Response
import com.smartcity.provider.ui.ResponseType
import com.smartcity.provider.ui.config.state.ConfigViewState
import com.smartcity.provider.ui.config.state.StoreFields
import com.smartcity.provider.util.*
import com.smartcity.provider.util.SuccessHandling.Companion.DONE_ALL_CATEGORIES
import kotlinx.coroutines.Job
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

@ConfigScope
class ConfigRepository
@Inject
constructor(
    val openApiConfigService: OpenApiConfigService,
    val sessionManager: SessionManager,
    val sharedPreferences: SharedPreferences,
    val sharedPrefsEditor: SharedPreferences.Editor
): JobManager("ConfigRepository")
{
    private val TAG: String = "AppDebug"
    
    fun attemptCreateStore(
        store: RequestBody,
        image: MultipartBody.Part?
    ): LiveData<DataState<ConfigViewState>> {
        /* val storeFieldErrors = StoreFields(name,description,address,category).isValidForLogin()
         if(!storeFieldErrors.equals(RegistrationFields.RegistrationError.none())){
             return returnErrorResponse(storeFieldErrors, ResponseType.Dialog())
         }*/

        return object: NetworkBoundResource<StoreResponse, Any, ConfigViewState>(
            sessionManager.isConnectedToTheInternet(),
            true,
            true,
            false
        ){
            // Ignore
            override fun loadFromCache(): LiveData<ConfigViewState> {
                return AbsentLiveData.create()
            }

            // Ignore
            override suspend fun updateLocalDb(cacheObject: Any?) {

            }

            // not used in this case
            override suspend fun createCacheRequestAndReturn() {

            }
            override suspend fun handleApiSuccessResponse(response: ApiSuccessResponse<StoreResponse>) {
                Log.d(TAG, "handleApiSuccessResponse: ${response}")

                onCompleteJob(
                    DataState.data(
                        data = null
                        ,
                        response = Response(
                            SuccessHandling.STORE_CREATION_DONE,
                            ResponseType.Toast()
                        )
                    )
                )
            }
            override fun createCall(): LiveData<GenericApiResponse<StoreResponse>> {
                return openApiConfigService.createStore(store,image)
            }

            override fun setJob(job: Job) {
                addJob("attemptCreateStore", job)
            }
        }.asLiveData()
    }

    fun attemptGetStoreCategories(
        id : Long
    ): LiveData<DataState<ConfigViewState>> {

        return object: NetworkBoundResource<ListGenericResponse<Category>, Any, ConfigViewState>(
            sessionManager.isConnectedToTheInternet(),
            true,
            true,
            false
        ){
            // Ignore
            override fun loadFromCache(): LiveData<ConfigViewState> {
                return AbsentLiveData.create()
            }

            // Ignore
            override suspend fun updateLocalDb(cacheObject: Any?) {

            }

            // not used in this case
            override suspend fun createCacheRequestAndReturn() {

            }
            override suspend fun handleApiSuccessResponse(response: ApiSuccessResponse<ListGenericResponse<Category>>) {


                Log.d(TAG, "handleApiSuccessResponse: ${response}")
                val list=response.body.results
                onCompleteJob(
                    DataState.data(
                        data = ConfigViewState(
                            storeFields = StoreFields(
                                storeCategory = list
                            )
                        )
                        ,
                        response = null
                    )
                )
            }
            override fun createCall(): LiveData<GenericApiResponse<ListGenericResponse<Category>>> {
                return openApiConfigService.getStoreCategories(
                    id = id
                )
            }

            override fun setJob(job: Job) {
                addJob("attemptGetStoreCategories", job)
            }
        }.asLiveData()
    }

    fun attemptSetStoreCategories(
        id : Long,
        categories : List<String>
    ): LiveData<DataState<ConfigViewState>> {
        return object: NetworkBoundResource<GenericResponse, Any, ConfigViewState>(
            sessionManager.isConnectedToTheInternet(),
            true,
            true,
            false
        ){
            // Ignore
            override fun loadFromCache(): LiveData<ConfigViewState> {
                return AbsentLiveData.create()
            }

            // Ignore
            override suspend fun updateLocalDb(cacheObject: Any?) {

            }

            // not used in this case
            override suspend fun createCacheRequestAndReturn() {

            }
            override suspend fun handleApiSuccessResponse(response: ApiSuccessResponse<GenericResponse>) {
                Log.d(TAG, "handleApiSuccessResponse: ${response}")
                sharedPrefsEditor.putBoolean(PreferenceKeys.CREATE_STORE_FLAG,true)
                sharedPrefsEditor.apply()
                onCompleteJob(
                    DataState.data(
                        data = null
                        ,
                        response = Response(
                            SuccessHandling.STORE_CATEGORIES_DONE,
                            ResponseType.None()
                        )
                    )
                )
            }
            override fun createCall(): LiveData<GenericApiResponse<GenericResponse>> {
                return openApiConfigService.setStoreCategories(
                    id = id,
                    categories = categories
                )
            }

            override fun setJob(job: Job) {
                addJob("attemptCreateStore", job)
            }
        }.asLiveData()
    }

    fun attemptGetAllCategories(
    ): LiveData<DataState<ConfigViewState>> {

        return object: NetworkBoundResource<ListGenericResponse<Category>, Any, ConfigViewState>(
            sessionManager.isConnectedToTheInternet(),
            true,
            true,
            false
        ){
            // Ignore
            override fun loadFromCache(): LiveData<ConfigViewState> {
                return AbsentLiveData.create()
            }

            // Ignore
            override suspend fun updateLocalDb(cacheObject: Any?) {

            }

            // not used in this case
            override suspend fun createCacheRequestAndReturn() {

            }
            override suspend fun handleApiSuccessResponse(response: ApiSuccessResponse<ListGenericResponse<Category>>) {


                Log.d(TAG, "handleApiSuccessResponse: ${response}")
                val list=response.body.results
                onCompleteJob(
                    DataState.data(
                        data = ConfigViewState(
                            storeFields = StoreFields(
                                allCategoryStore = list
                            )
                        )
                        ,
                        response = Response(
                            DONE_ALL_CATEGORIES,
                            ResponseType.None())
                    )
                )
            }
            override fun createCall(): LiveData<GenericApiResponse<ListGenericResponse<Category>>> {
                return openApiConfigService.getAllCategories()
            }

            override fun setJob(job: Job) {
                addJob("attemptGetAllCategories", job)
            }
        }.asLiveData()
    }
}