package com.smartcity.provider.ui

interface DataStateChangeListener{

    fun onDataStateChange(dataState: DataState<*>?)

    fun expandAppBar()

    fun hideSoftKeyboard()

    fun isStoragePermissionGranted(): Boolean
}