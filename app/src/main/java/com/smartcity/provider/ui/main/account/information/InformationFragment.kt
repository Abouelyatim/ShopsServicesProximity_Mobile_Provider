package com.smartcity.provider.ui.main.account.information

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.RequestManager
import com.smartcity.provider.R
import com.smartcity.provider.models.StoreInformation
import com.smartcity.provider.ui.*
import com.smartcity.provider.ui.main.account.BaseAccountFragment
import com.smartcity.provider.ui.main.account.state.ACCOUNT_VIEW_STATE_BUNDLE_KEY
import com.smartcity.provider.ui.main.account.state.AccountStateEvent
import com.smartcity.provider.ui.main.account.state.AccountViewState
import com.smartcity.provider.ui.main.account.viewmodel.AccountViewModel
import com.smartcity.provider.util.ErrorHandling.Companion.ERROR_FILL_ALL_INFORMATION
import com.smartcity.provider.util.ErrorHandling.Companion.ERROR_INVALID_PHONE_NUMBER_FORMAT
import com.smartcity.provider.util.SuccessHandling
import kotlinx.android.synthetic.main.fragment_information.*
import me.ibrahimsn.lib.PhoneNumberKit
import javax.inject.Inject


class InformationFragment
@Inject
constructor(
    private val viewModelFactory: ViewModelProvider.Factory,
    private val requestManager: RequestManager
): BaseAccountFragment(R.layout.fragment_information){

    lateinit var defaultPhoneNumberKit:PhoneNumberKit
    lateinit var phoneNumberKit:PhoneNumberKit

    val viewModel: AccountViewModel by viewModels{
        viewModelFactory
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putParcelable(
            ACCOUNT_VIEW_STATE_BUNDLE_KEY,
            viewModel.viewState.value
        )
        super.onSaveInstanceState(outState)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cancelActiveJobs()
        // Restore state after process death
        savedInstanceState?.let { inState ->
            (inState[ACCOUNT_VIEW_STATE_BUNDLE_KEY] as AccountViewState?)?.let { viewState ->
                viewModel.setViewState(viewState)
            }
        }
    }

    override fun cancelActiveJobs(){
        viewModel.cancelActiveJobs()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(false)
        setHasOptionsMenu(true)
        stateChangeListener.expandAppBar()
        stateChangeListener.displayBottomNavigation(false)


        subscribeObservers()
        setTextInput()

        save_information_button.setOnClickListener {
            saveStoreInformation()
        }


    }

    private fun saveStoreInformation(){
        if(validPhoneNumber()){
            viewModel.setStateEvent(
                AccountStateEvent.SetStoreInformation(
                    StoreInformation(
                        -1,
                        input_address.text.toString(),
                        input_phone_number.text.toString(),
                        input_default_phone_number.text.toString()
                    )
                )
            )
        }
    }

    private fun subscribeObservers() {
        viewModel.dataState.observe(viewLifecycleOwner, Observer { dataState ->
            stateChangeListener.onDataStateChange(dataState)
            if(dataState != null){
                dataState.data?.let { data ->
                    data.response?.peekContent()?.let{ response ->

                        if(!data.response.hasBeenHandled){
                            if (response.message==SuccessHandling.CREATION_DONE){
                                navAccount()
                            }
                        }

                    }
                }
            }
        })
    }

    fun navAccount(){
        findNavController().popBackStack()
    }
    private fun setTextInput(){
        defaultPhoneNumberKit = PhoneNumberKit(context!!)
        defaultPhoneNumberKit.attachToInput(phone_number, 213)
        defaultPhoneNumberKit.setupCountryPicker(
            activity = activity as AppCompatActivity,
            searchEnabled = true
        )
        default_phone_number.helperText="*Required\nPhone number for SmartCity"

        phoneNumberKit = PhoneNumberKit(context!!)
        phoneNumberKit.attachToInput(default_phone_number, 213)
        phoneNumberKit.setupCountryPicker(
            activity = activity as AppCompatActivity,
            searchEnabled = true
        )
        phone_number.helperText="Phone number for clients"
    }

    private fun validPhoneNumber():Boolean{
        if(!defaultPhoneNumberKit.isValid || !phoneNumberKit.isValid){
            showErrorDialog(ERROR_INVALID_PHONE_NUMBER_FORMAT)
            return false
        }
        if(input_address.text.toString().isEmpty()){
            showErrorDialog(ERROR_FILL_ALL_INFORMATION)
            return false
        }
        return true
    }

    fun showErrorDialog(errorMessage: String){
        stateChangeListener.onDataStateChange(
            DataState(
                Event(StateError(Response(errorMessage, ResponseType.Dialog()))),
                Loading(isLoading = false),
                Data(Event.dataEvent(null), null)
            )
        )
    }
}