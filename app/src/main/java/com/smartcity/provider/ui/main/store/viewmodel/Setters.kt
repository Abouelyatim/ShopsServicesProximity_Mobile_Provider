package com.smartcity.provider.ui.main.store.viewmodel

import com.smartcity.provider.models.product.Product
import com.smartcity.provider.ui.main.store.state.StoreViewState

fun StoreViewModel.clearViewProductList(){
    val update = getCurrentViewStateOrNew()
    update.viewProductList= StoreViewState.ViewProductList()
    setViewState(update)
}

fun StoreViewModel.setViewProductList(productList: StoreViewState.ViewProductList){
    val update = getCurrentViewStateOrNew()
    update.viewProductList=productList
    setViewState(update)
}

fun StoreViewModel.setViewCustomCategoryFields(viewCustomCategoryFields: StoreViewState.ViewCustomCategoryFields){
    val update = getCurrentViewStateOrNew()

    if(update.viewCustomCategoryFields == viewCustomCategoryFields){
        return
    }
    update.viewCustomCategoryFields = viewCustomCategoryFields
    setViewState(update)
}
fun StoreViewModel.setViewProductFields(product: Product){
    val update = getCurrentViewStateOrNew()
    update.viewProductFields.product = product
    setViewState(update)
}
fun StoreViewModel.setChoisesMap(map: MutableMap<String, String>){
    val update = getCurrentViewStateOrNew()
    update.choisesMap.choises = map
    setViewState(update)
}
fun StoreViewModel.clearChoisesMap(){
    val update = getCurrentViewStateOrNew()
    update.choisesMap= StoreViewState.ChoisesMap()
    setViewState(update)
}
fun StoreViewModel.setCustomCategoryRecyclerPosition(postion:Int){
    val update = getCurrentViewStateOrNew()
    update.customCategoryRecyclerPosition=postion
    setViewState(update)
}