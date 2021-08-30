package com.smartcity.provider.ui.main.custom_category.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.smartcity.provider.di.main.MainScope
import com.smartcity.provider.repository.main.CustomCategoryRepositoryImpl
import com.smartcity.provider.session.SessionManager
import com.smartcity.provider.ui.BaseViewModel
import com.smartcity.provider.ui.DataState
import com.smartcity.provider.ui.Loading
import com.smartcity.provider.ui.main.custom_category.state.CustomCategoryStateEvent
import com.smartcity.provider.ui.main.custom_category.state.CustomCategoryStateEvent.*
import com.smartcity.provider.ui.main.custom_category.state.CustomCategoryViewState
import com.smartcity.provider.ui.main.custom_category.state.CustomCategoryViewState.*
import com.smartcity.provider.util.AbsentLiveData
import java.util.*
import javax.inject.Inject

@MainScope
class CustomCategoryViewModel
@Inject
constructor(
    val customCategoryRepository: CustomCategoryRepositoryImpl,
    val sessionManager: SessionManager
): BaseViewModel<CustomCategoryStateEvent, CustomCategoryViewState>() {

    override fun handleStateEvent(stateEvent: CustomCategoryStateEvent): LiveData<DataState<CustomCategoryViewState>> {

        when(stateEvent){
            is CustomCategoryMainEvent ->{
                return sessionManager.cachedToken.value?.let { authToken ->
                    customCategoryRepository.attemptCustomCategoryMain(
                        authToken.account_pk!!.toLong()
                    )
                }?: AbsentLiveData.create()

            }
            is CreateCustomCategoryEvent ->{
                return sessionManager.cachedToken.value?.let { authToken ->

                    customCategoryRepository.attemptCreateCustomCategory(
                        authToken.account_pk!!.toLong(),
                        stateEvent.name
                    )
                }?: AbsentLiveData.create()
            }
            is DeleteCustomCategoryEvent -> {
                return customCategoryRepository.attemptdeleteCustomCategory(
                    stateEvent.id
                )
            }
            is UpdateCustomCategoryEvent -> {
                return customCategoryRepository.attemptUpdateCustomCategory(
                    stateEvent.id,
                    stateEvent.name,
                    stateEvent.provider
                )
            }

            is CreateProductEvent ->{
                return customCategoryRepository.attemptCreateProduct(
                    stateEvent.product,
                    stateEvent.productImagesFile,
                    stateEvent.variantesImagesFile,
                    stateEvent.productObject
                )
            }
            is UpdateProductEvent ->{
                return customCategoryRepository.attemptUpdateProduct(
                    stateEvent.product,
                    stateEvent.productImagesFile,
                    stateEvent.variantesImagesFile,
                    stateEvent.productObject
                )
            }

            is DeleteProductEvent ->{
                return customCategoryRepository.attemptDeleteProduct(
                    stateEvent.id
                )
            }

            is ProductMainEvent ->{
                return customCategoryRepository.attemptProductMain(
                    stateEvent.id
                )
            }

            is UpdateProductsCustomCategoryEvent ->{
                return customCategoryRepository.attemptUpdateProductsCustomCategory(
                    stateEvent.products,
                    stateEvent.category
                )
            }

            is None -> {
                return liveData {
                    emit(
                        DataState<CustomCategoryViewState>(
                            null,
                            Loading(false),
                            null
                        )
                    )
                }
            }
        }

    }

    override fun initNewViewState(): CustomCategoryViewState {
        return CustomCategoryViewState()
    }

    fun cancelActiveJobs(){
        customCategoryRepository.cancelActiveJobs()
        handlePendingData()
    }

    fun handlePendingData(){
        setStateEvent(None())
    }

    override fun onCleared() {
        super.onCleared()
        cancelActiveJobs()
    }
}










