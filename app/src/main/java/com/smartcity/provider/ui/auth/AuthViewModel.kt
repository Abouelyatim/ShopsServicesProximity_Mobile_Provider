package com.smartcity.provider.ui.auth

import android.net.Uri
import android.util.Log
import androidx.lifecycle.*
import com.smartcity.provider.di.auth.AuthScope
import com.smartcity.provider.models.AuthToken
import com.smartcity.provider.repository.auth.AuthRepository
import com.smartcity.provider.session.SessionManager
import com.smartcity.provider.ui.BaseViewModel
import com.smartcity.provider.ui.DataState
import com.smartcity.provider.ui.Loading
import com.smartcity.provider.ui.auth.state.*
import com.smartcity.provider.ui.auth.state.AuthStateEvent.*
import com.smartcity.provider.ui.main.blog.viewmodel.BlogViewModel
import com.smartcity.provider.util.AbsentLiveData
import okhttp3.MediaType
import okhttp3.RequestBody
import javax.inject.Inject

@AuthScope
class AuthViewModel
@Inject
constructor(
    val authRepository: AuthRepository,
    private val sessionManager: SessionManager
): BaseViewModel<AuthStateEvent, AuthViewState>()
{
    override fun handleStateEvent(stateEvent: AuthStateEvent): LiveData<DataState<AuthViewState>> {
        when(stateEvent){

            is LoginAttemptEvent -> {
                return authRepository.attemptLogin(
                    stateEvent.email,
                    stateEvent.password
                )
            }

            is RegisterAttemptEvent -> {
                return authRepository.attemptRegistration(
                    stateEvent.email,
                    stateEvent.username,
                    stateEvent.password,
                    stateEvent.confirm_password
                )
            }

            is CheckPreviousAuthEvent -> {
                return authRepository.checkPreviousAuthUser()
            }


            is CreateStoreAttemptEvent ->{
                return sessionManager.cachedToken.value?.let { authToken ->

                    /*val map = HashMap<String, Any>()
                    map.put("name", stateEvent.name)
                    map.put("description", stateEvent.description)
                    map.put("address", stateEvent.address)
                    map.put("provider", )
                    map.put("categories", stateEvent.category)*/

                    authRepository.attemptCreateStore(
                        stateEvent.name,
                        stateEvent.description,
                        stateEvent.address,
                        authToken.account_pk!!.toLong(),
                        stateEvent.category,
                        stateEvent.image
                    )
                }?: AbsentLiveData.create()

            }
            is GetCategoryStore ->{

                return authRepository.attemptGetCategoryStore()
            }

            is None ->{
                return liveData {
                    emit(
                        DataState(
                            null,
                            Loading(false),
                            null
                        )
                    )
                }
            }
        }
    }

    override fun initNewViewState(): AuthViewState {
        return AuthViewState()
    }

    fun setRegistrationFields(registrationFields: RegistrationFields){
        val update = getCurrentViewStateOrNew()
        if(update.registrationFields == registrationFields){
            return
        }
        update.registrationFields = registrationFields
        setViewState(update)
    }

    fun setLoginFields(loginFields: LoginFields){
        val update = getCurrentViewStateOrNew()
        if(update.loginFields == loginFields){
            return
        }
        update.loginFields = loginFields
        setViewState(update)
    }

    fun setAuthToken(authToken: AuthToken){
        val update = getCurrentViewStateOrNew()
        if(update.authToken == authToken){
            return
        }
        update.authToken = authToken
        setViewState(update)
    }
    fun setCategoryStore( categoryStore: CategoryStore){
        val update = getCurrentViewStateOrNew()
        if(update.categoryStore == categoryStore){
            return
        }
        update.categoryStore = categoryStore
        setViewState(update)
    }

    fun isRegistred(): Boolean{
        getCurrentViewStateOrNew().let {
            return it.registrationState.isRegistred
        }
    }

    fun getCategoryStore():List<String>?{
        getCurrentViewStateOrNew().let {
            return it.categoryStore.listCategoryStore
        }
    }
    fun setStoreFields(storeFields: StoreFields ){
        val update = getCurrentViewStateOrNew()
        if(update.storeFields == storeFields){
            return
        }
        update.storeFields = storeFields
        setViewState(update)
    }
    fun setNewStoreFields( name: String? ,
                           description: String?,
                           address: String?,
                           category: List<String>?,
                           newImageUri: Uri? ){

        val update = getCurrentViewStateOrNew()
        val newStoreFields = update.storeFields

        name?.let{ newStoreFields.store_name = it }
        description?.let{ newStoreFields.store_description = it }
        address?.let{ newStoreFields.store_address = it }
        category?.let{newStoreFields.store_category = it}
        newImageUri?.let{newStoreFields.newImageUri = it}

        update.storeFields = newStoreFields
        _viewState.value = update
    }

    fun setIsRegistred(isRegistred: Boolean){
        val update = getCurrentViewStateOrNew()
        update.registrationState.isRegistred = isRegistred
        setViewState(update)
    }


    fun cancelActiveJobs(){
        handlePendingData()
        authRepository.cancelActiveJobs()
    }

    fun handlePendingData(){
        setStateEvent(None())
    }

    override fun onCleared() {
        super.onCleared()
        cancelActiveJobs()
    }
}
































