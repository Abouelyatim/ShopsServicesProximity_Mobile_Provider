package com.smartcity.provider.repository.main

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import com.smartcity.provider.api.GenericResponse
import com.smartcity.provider.api.main.OpenApiMainService
import com.smartcity.provider.api.main.responses.ListCustomCategoryResponse
import com.smartcity.provider.api.main.responses.ListGenericResponse
import com.smartcity.provider.api.main.responses.ListProductResponse
import com.smartcity.provider.di.main.MainScope
import com.smartcity.provider.models.*
import com.smartcity.provider.models.product.Product
import com.smartcity.provider.repository.JobManager
import com.smartcity.provider.repository.NetworkBoundResource
import com.smartcity.provider.session.SessionManager
import com.smartcity.provider.ui.DataState
import com.smartcity.provider.ui.Response
import com.smartcity.provider.ui.ResponseType
import com.smartcity.provider.ui.main.account.state.AccountViewState
import com.smartcity.provider.ui.main.custom_category.state.CustomCategoryViewState
import com.smartcity.provider.util.*
import com.smartcity.provider.util.SuccessHandling.Companion.RESPONSE_GET_NOTIFICATION_SETTINGS_DONE
import com.smartcity.provider.util.SuccessHandling.Companion.RESPONSE_SAVE_NOTIFICATION_SETTINGS_DONE
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.withContext
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
                            storeInformationFields= AccountViewState.StoreInformationFields(
                                storeInformation = response.body
                            )
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

    fun attemptAllCategory(
    ): LiveData<DataState<AccountViewState>> {
        return object :
            NetworkBoundResource<ListGenericResponse<Category>, List<Category>, AccountViewState>(
                sessionManager.isConnectedToTheInternet(),
                true,
                true,
                false
            ) {
            // Ignore
            override suspend fun createCacheRequestAndReturn() {

            }

            override suspend fun handleApiSuccessResponse(response: ApiSuccessResponse<ListGenericResponse<Category>>) {
                Log.d(TAG, "handleApiSuccessResponse: ${response}")


                onCompleteJob(
                    DataState.data(
                        data = AccountViewState(
                            storeInformationFields = AccountViewState.StoreInformationFields(
                                categoryList = response.body.results
                            )
                        ),
                        response = Response(
                            SuccessHandling.DONE_ALL_CATEGORIES,
                            ResponseType.None()
                        )
                    )

                )
            }


            override fun createCall(): LiveData<GenericApiResponse<ListGenericResponse<Category>>> {
                return openApiMainService.getAllCategory()
            }

            // Ignore
            override fun loadFromCache(): LiveData<AccountViewState> {
                return AbsentLiveData.create()
            }

            // Ignore
            override suspend fun updateLocalDb(cacheObject: List<Category>?) {

            }

            override fun setJob(job: Job) {
                addJob("attemptAllCategory", job)
            }

        }.asLiveData()
    }

    fun attemptGetCustomCategories(
        id: Long
    ): LiveData<DataState<AccountViewState>> {
        return object :
            NetworkBoundResource<ListCustomCategoryResponse, List<CustomCategory>, AccountViewState>(
                sessionManager.isConnectedToTheInternet(),
                true,
                true,
                false
            ) {
            // Ignore
            override suspend fun createCacheRequestAndReturn() {

            }

            override suspend fun handleApiSuccessResponse(response: ApiSuccessResponse<ListCustomCategoryResponse>) {
                Log.d(TAG, "handleApiSuccessResponse: ${response}")
                val customCategoryList: ArrayList<CustomCategory> = ArrayList()

                for (customCategoryResponse in response.body.results) {
                    customCategoryList.add(
                        CustomCategory(
                            pk = customCategoryResponse.pk,
                            name = customCategoryResponse.name,
                            provider = customCategoryResponse.provider
                        )
                    )
                }

                onCompleteJob(
                    DataState.data(
                        data = AccountViewState(
                            discountFields = AccountViewState.DiscountFields(customCategoryList)
                        ),
                        response = null
                    )
                )
            }


            override fun createCall(): LiveData<GenericApiResponse<ListCustomCategoryResponse>> {
                return openApiMainService.getAllcustomCategory(
                    id = id
                )
            }

            // Ignore
            override fun loadFromCache(): LiveData<AccountViewState> {
                return AbsentLiveData.create()
            }

            // Ignore
            override suspend fun updateLocalDb(cacheObject: List<CustomCategory>?) {

            }

            override fun setJob(job: Job) {
                addJob("attemptGetCustomCategories", job)
            }

        }.asLiveData()
    }

    fun attemptGetProducts(
        id: Long
    ): LiveData<DataState<AccountViewState>> {
        return object :
            NetworkBoundResource<ListProductResponse, List<Product>, AccountViewState>(
                sessionManager.isConnectedToTheInternet(),
                true,
                true,
                false
            ) {
            // Ignore
            override suspend fun createCacheRequestAndReturn() {

            }

            override suspend fun handleApiSuccessResponse(response: ApiSuccessResponse<ListProductResponse>) {
                withContext(Dispatchers.Main){
                    Log.d(TAG, "handleApiSuccessResponse: ${response}")
                    Log.d(TAG,id.toString())
                    onCompleteJob(
                        DataState.data(
                            data = AccountViewState(
                                discountFields = AccountViewState.DiscountFields(
                                    productsList = response.body.results
                                )
                            ),
                            response = null
                        )
                    )
                }

            }


            override fun createCall(): LiveData<GenericApiResponse<ListProductResponse>> {
                return openApiMainService.getAllProductByCategory(
                    id = id
                )
            }

            // Ignore
            override fun loadFromCache(): LiveData<AccountViewState> {
                return AbsentLiveData.create()
            }

            // Ignore
            override suspend fun updateLocalDb(cacheObject: List<Product>?) {

            }

            override fun setJob(job: Job) {
                addJob("attemptGetProducts", job)
            }

        }.asLiveData()
    }

    fun attemptCreateOffer(
        offer: Offer
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

                withContext(Dispatchers.Main){
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

            }


            override fun createCall(): LiveData<GenericApiResponse<GenericResponse>> {
                return openApiMainService.createOffer(
                    offer = offer
                )
            }

            // Ignore
            override fun loadFromCache(): LiveData<AccountViewState> {
                return AbsentLiveData.create()
            }

            // Ignore
            override suspend fun updateLocalDb(cacheObject: Any?) {

            }

            override fun setJob(job: Job) {
                addJob("attemptCreateOffer", job)
            }

        }.asLiveData()
    }

    fun attemptGetOffers(
        id:Long,
        offerState: OfferState?
    ): LiveData<DataState<AccountViewState>> {
        return object: NetworkBoundResource<ListGenericResponse<Offer>, Offer, AccountViewState>(
            sessionManager.isConnectedToTheInternet(),
            true,
            true,
            false
        ){


            // not applicable
            override suspend fun createCacheRequestAndReturn() {

            }

            override suspend fun handleApiSuccessResponse(response: ApiSuccessResponse<ListGenericResponse<Offer>>) {
                Log.d(TAG, "handleApiSuccessResponse: ${response}")

                onCompleteJob(
                    DataState.data(
                        data = AccountViewState(
                            discountOfferList = AccountViewState.DiscountOfferList(
                                offersList= response.body.results
                            )
                        ),
                        response = Response(
                            SuccessHandling.DONE_Offers,
                            ResponseType.None()
                        )
                    )
                )
            }

            // not applicable
            override fun loadFromCache(): LiveData<AccountViewState> {
                return AbsentLiveData.create()
            }

            override fun createCall(): LiveData<GenericApiResponse<ListGenericResponse<Offer>>> {
                return openApiMainService.getAllOffers(
                    id= id,
                    status= offerState
                )
            }

            // not applicable
            override suspend fun updateLocalDb(cacheObject: Offer?) {
            }

            override fun setJob(job: Job) {
                addJob("attemptGetOffers", job)
            }


        }.asLiveData()
    }

    fun attemptDeleteOffer(
        id: Long): LiveData<DataState<AccountViewState>> {
        return object :
            NetworkBoundResource<GenericResponse, Offer, AccountViewState>(
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

                if(response.body.response == SuccessHandling.DELETE_DONE){
                    onCompleteJob(
                        DataState.data(
                            data = null
                            ,
                            response = Response(
                                SuccessHandling.DELETE_DONE,
                                ResponseType.SnackBar()
                            )
                        )
                    )
                }else{
                    onCompleteJob(
                        DataState.error(
                            Response(
                                ErrorHandling.ERROR_UNKNOWN,
                                ResponseType.Dialog()
                            )
                        )
                    )
                }

            }

            override fun createCall(): LiveData<GenericApiResponse<GenericResponse>> {
                return openApiMainService.deleteOffer(
                    id=id
                )
            }

            override fun loadFromCache(): LiveData<AccountViewState> {
                return AbsentLiveData.create()
            }



            override fun setJob(job: Job) {
                addJob("attemptDeleteOffer", job)
            }

            override suspend fun updateLocalDb(cacheObject: Offer?) {

            }

        }.asLiveData()
    }

    fun attemptUpdateOffer(
        offer: Offer
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

                withContext(Dispatchers.Main){
                    Log.d(TAG, "handleApiSuccessResponse: ${response}")

                    onCompleteJob(
                        DataState.data(
                            data = null
                            ,
                            response = Response(
                                SuccessHandling.PRODUCT_UPDATE_DONE,
                                ResponseType.Toast()
                            )
                        )
                    )
                }

            }


            override fun createCall(): LiveData<GenericApiResponse<GenericResponse>> {
                return openApiMainService.updateOffer(
                    offer = offer
                )
            }

            // Ignore
            override fun loadFromCache(): LiveData<AccountViewState> {
                return AbsentLiveData.create()
            }

            // Ignore
            override suspend fun updateLocalDb(cacheObject: Any?) {

            }

            override fun setJob(job: Job) {
                addJob("attemptUpdateOffer", job)
            }

        }.asLiveData()
    }

    fun attemptCreateFlashDeal(
        flashDeal: FlashDeal
    ): LiveData<DataState<AccountViewState>> {

        val flashDealErrors = flashDeal.isValidForCreation()

        if(flashDealErrors != FlashDeal.CreateFlashError.none()){
            return returnErrorResponse(flashDealErrors, ResponseType.Dialog())
        }

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

                withContext(Dispatchers.Main){
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

            }


            override fun createCall(): LiveData<GenericApiResponse<GenericResponse>> {
                return openApiMainService.createFlashDeal(
                    flashDeal = flashDeal
                )
            }

            // Ignore
            override fun loadFromCache(): LiveData<AccountViewState> {
                return AbsentLiveData.create()
            }

            // Ignore
            override suspend fun updateLocalDb(cacheObject: Any?) {

            }

            override fun setJob(job: Job) {
                addJob("attemptCreateFlashDeal", job)
            }

        }.asLiveData()
    }

    fun attemptGetFlashDeals(
        id:Long
    ): LiveData<DataState<AccountViewState>> {
        return object: NetworkBoundResource<ListGenericResponse<FlashDeal>, FlashDeal, AccountViewState>(
            sessionManager.isConnectedToTheInternet(),
            true,
            true,
            false
        ){


            // not applicable
            override suspend fun createCacheRequestAndReturn() {

            }

            override suspend fun handleApiSuccessResponse(response: ApiSuccessResponse<ListGenericResponse<FlashDeal>>) {
                Log.d(TAG, "handleApiSuccessResponse: ${response}")

                onCompleteJob(
                    DataState.data(
                        data = AccountViewState(
                            flashDealsFields = AccountViewState.FlashDealsFields(
                                flashDealsList= response.body.results
                            )
                        ),
                        response = Response(
                            SuccessHandling.DONE_Flashes,
                            ResponseType.None()
                        )
                    )
                )
            }

            // not applicable
            override fun loadFromCache(): LiveData<AccountViewState> {
                return AbsentLiveData.create()
            }

            override fun createCall(): LiveData<GenericApiResponse<ListGenericResponse<FlashDeal>>> {
                return openApiMainService.getFlashDeals(
                    id= id
                )
            }

            // not applicable
            override suspend fun updateLocalDb(cacheObject: FlashDeal?) {
            }

            override fun setJob(job: Job) {
                addJob("attemptGetFlashDeals", job)
            }


        }.asLiveData()
    }

    fun attemptGetSearchFlashDeals(
        id:Long,
        startDate: String?,
        endDate: String?
    ): LiveData<DataState<AccountViewState>> {
        return object: NetworkBoundResource<ListGenericResponse<FlashDeal>, FlashDeal, AccountViewState>(
            sessionManager.isConnectedToTheInternet(),
            true,
            true,
            false
        ){


            // not applicable
            override suspend fun createCacheRequestAndReturn() {

            }

            override suspend fun handleApiSuccessResponse(response: ApiSuccessResponse<ListGenericResponse<FlashDeal>>) {
                Log.d(TAG, "handleApiSuccessResponse: ${response}")

                onCompleteJob(
                    DataState.data(
                        data = AccountViewState(
                            flashDealsFields = AccountViewState.FlashDealsFields(
                                searchFlashDealsList= response.body.results
                            )
                        ),
                        response = Response(
                            SuccessHandling.DONE_Flashes,
                            ResponseType.None()
                        )
                    )
                )
            }

            // not applicable
            override fun loadFromCache(): LiveData<AccountViewState> {
                return AbsentLiveData.create()
            }

            override fun createCall(): LiveData<GenericApiResponse<ListGenericResponse<FlashDeal>>> {
                return openApiMainService.getSearchFlashDeals(
                    id= id,
                    startDate = startDate,
                    endDate = endDate
                )
            }

            // not applicable
            override suspend fun updateLocalDb(cacheObject: FlashDeal?) {
            }

            override fun setJob(job: Job) {
                addJob("attemptGetSearchFlashDeals", job)
            }


        }.asLiveData()
    }

    private fun returnErrorResponse(errorMessage: String, responseType: ResponseType): LiveData<DataState<AccountViewState>>{
        Log.d(TAG, "returnErrorResponse: ${errorMessage}")

        return object: LiveData<DataState<AccountViewState>>(){
            override fun onActive() {
                super.onActive()
                value = DataState.error(
                    Response(
                        errorMessage,
                        responseType
                    )
                )
            }
        }
    }
}