package com.smartcity.provider.ui.main.custom_category

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.smartcity.provider.di.main.MainScope
import com.smartcity.provider.models.CustomCategory
import com.smartcity.provider.models.product.Attribute
import com.smartcity.provider.models.product.Product
import com.smartcity.provider.models.product.ProductVariants
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
import kotlin.collections.HashSet

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

    fun setCustomCategoryFields(customCategoryFields: CustomCategoryFields){
        val update = getCurrentViewStateOrNew()
        if(update.customCategoryFields == customCategoryFields){
            return
        }
        update.customCategoryFields = customCategoryFields
        setViewState(update)
    }
    fun getCustomCategoryFields():List<CustomCategory>{
        getCurrentViewStateOrNew().let {
            return it.customCategoryFields.customCategoryList
        }
    }

    fun setSelectedCustomCategory(selectedCustomCategory: CustomCategory){
        val update = getCurrentViewStateOrNew()
        update.selectedCustomCategory.customCategory = selectedCustomCategory
        setViewState(update)
    }
    fun setProductFields(productFields: ProductFields){
        val update = getCurrentViewStateOrNew()
        update.productFields = productFields
        setViewState(update)
    }

    fun getProductFields():ProductFields{
        getCurrentViewStateOrNew().let {
            return it.productFields
        }
    }

    fun getSelectedCustomCategory():CustomCategory?{
        getCurrentViewStateOrNew().let {
            return it.selectedCustomCategory.customCategory
        }
    }

    fun setNewOption(attribute: Attribute?){
        val update = getCurrentViewStateOrNew()
        update.newOption.attribute = attribute
        setViewState(update)
    }



    fun getNewOption():Attribute?{
        getCurrentViewStateOrNew().let {
            return it.newOption.attribute
        }
    }

    fun setOptionList(attribute: Attribute){
        val update = getCurrentViewStateOrNew()
        val optionList= update.productFields.attributeList
        optionList.find { it.name.equals(attribute.name) }.let {
            if (it != null) {
                it.attributeValues=attribute.attributeValues
            }else{
                optionList.add(attribute)
            }
        }
        update.productFields.attributeList=optionList
        setViewState(update)
    }



    fun setOptionList(attributeSet: HashSet <Attribute>){
        val update = getCurrentViewStateOrNew()
        update.productFields.attributeList=attributeSet
        setViewState(update)
    }

    fun getOptionList():HashSet <Attribute>{
        getCurrentViewStateOrNew().let {
            return it.productFields.attributeList
        }
    }



    fun setProductVariantsList(productVariants: MutableList<ProductVariants>){
        val update = getCurrentViewStateOrNew()
        update.productFields.productVariantList=productVariants
        setViewState(update)
    }

    fun getProductVariantsList():List<ProductVariants>{
        getCurrentViewStateOrNew().let {
            return it.productFields.productVariantList
        }
    }

    fun setCopyImage(uri: Uri?){
        val update = getCurrentViewStateOrNew()
        update.copyImage.copyImage=uri
        setViewState(update)
    }

    fun getCopyImage():Uri?{
        getCurrentViewStateOrNew().let {
            return it.copyImage.copyImage
        }
    }

    fun setProductList(productList: ProductList){
        val update = getCurrentViewStateOrNew()
        update.productList=productList
        setViewState(update)
    }

    fun getProductList():List<Product>{
        getCurrentViewStateOrNew().let {
            return it.productList.products
        }
    }

    fun isEmptyProductFields():Boolean{
        val update = getCurrentViewStateOrNew()
        if (update.productFields.name==""){
            return true
        }
        return false
    }
    fun setProductImageList(image: Uri){
        val update = getCurrentViewStateOrNew()
        update.productFields.productImageList.add(image)
        setViewState(update)
    }

    fun setProductImageList(images: MutableList<Uri>){
        val update = getCurrentViewStateOrNew()
        update.productFields.productImageList=images
        setViewState(update)
    }


    fun getProductImageList():List<Uri>?{
        getCurrentViewStateOrNew().let {
            return it.productFields.productImageList
        }
    }




    fun setSelectedProductVariant(productVariants: ProductVariants?){
        val update = getCurrentViewStateOrNew()
        update.selectedProductVariant.variante = productVariants
        setViewState(update)
    }



    fun getSelectedProductVariant():ProductVariants?{
        getCurrentViewStateOrNew().let {
            return it.selectedProductVariant.variante
        }
    }

    fun setViewProductFields(product: Product){
        val update = getCurrentViewStateOrNew()
        update.viewProductFields.product = product
        setViewState(update)
    }

    fun getViewProductFields():Product?{
        getCurrentViewStateOrNew().let {
            return it.viewProductFields.product
        }
    }



    fun setChoisesMap(map: MutableMap<String, String>){
        val update = getCurrentViewStateOrNew()
        update.choisesMap.choises = map
        setViewState(update)
    }

    fun getChoisesMap():MutableMap<String, String>{
        getCurrentViewStateOrNew().let {
            return it.choisesMap.choises
        }
    }
    fun clearChoisesMap(){
        val update = getCurrentViewStateOrNew()
        update.choisesMap=ChoisesMap()
        setViewState(update)
    }




    fun clearProductFields(){
        val update = getCurrentViewStateOrNew()
        update.newOption= NewOption()
       /* update.productFields.description=""
        update.productFields.name=""
        update.productFields.price=""
        update.productFields.quantity=""*/
        update.productFields= ProductFields()
        update.selectedProductVariant= SelectedProductVariant()
        setViewState(update)
    }

    fun clearViewProductFields(){
        val update = getCurrentViewStateOrNew()
        update.viewProductFields= ViewProductFields()
        setViewState(update)
    }

    fun clearProductList(){
        val update = getCurrentViewStateOrNew()
        update.productList=ProductList()
    }

    fun handlePendingData(){
        setStateEvent(None())
    }

    override fun onCleared() {
        super.onCleared()
        cancelActiveJobs()
    }
}










