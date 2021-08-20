package com.smartcity.provider.ui

interface DataStateChangeListener{

    fun onDataStateChange(dataState: DataState<*>?)

    fun expandAppBar()

    fun displayBottomNavigation(bool: Boolean)

    fun hideSoftKeyboard()

    fun isStoragePermissionGranted(): Boolean

    fun isFineLocationPermissionGranted(): Boolean

    fun isCameraPermissionGranted(): Boolean
}