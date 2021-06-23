package com.smartcity.provider.ui.main.account.information

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.smartcity.provider.R
import com.smartcity.provider.models.StoreInformation
import com.smartcity.provider.ui.*
import com.smartcity.provider.ui.main.account.BaseAccountFragment
import com.smartcity.provider.ui.main.account.information.adapters.SelectedCategoriesValueAdapter
import com.smartcity.provider.ui.main.account.state.ACCOUNT_VIEW_STATE_BUNDLE_KEY
import com.smartcity.provider.ui.main.account.state.AccountStateEvent
import com.smartcity.provider.ui.main.account.state.AccountViewState
import com.smartcity.provider.ui.main.account.viewmodel.*
import com.smartcity.provider.util.ErrorHandling.Companion.ERROR_FILL_ALL_INFORMATION
import com.smartcity.provider.util.ErrorHandling.Companion.ERROR_INVALID_PHONE_NUMBER_FORMAT
import com.smartcity.provider.util.SuccessHandling
import com.smartcity.provider.util.TopSpacingItemDecoration
import kotlinx.android.synthetic.main.fragment_information.*
import me.ibrahimsn.lib.PhoneNumberKit
import javax.inject.Inject


class InformationFragment
@Inject
constructor(
    private val viewModelFactory: ViewModelProvider.Factory,
    private val requestManager: RequestManager
): BaseAccountFragment(R.layout.fragment_information)
{

    private lateinit var selectedCategoriesValueAdapter: SelectedCategoriesValueAdapter

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

        initRecyclerView()
        subscribeObservers()
        setTextInput()

        save_information_button.setOnClickListener {
            saveStoreInformation()
        }

        viewModel.setStateEvent(
            AccountStateEvent.GetStoreInformation()
        )

        add_category_button.setOnClickListener {
            navCategory()
        }
    }

    private fun navCategory() {
        findNavController().navigate(R.id.action_informationFragment_to_categoryFragment)
    }

    private fun initRecyclerView() {
        selected_category_recyclerview.apply {
            layoutManager = FlexboxLayoutManager(context)
            (layoutManager as FlexboxLayoutManager).justifyContent = JustifyContent.CENTER
            (layoutManager as FlexboxLayoutManager).flexWrap= FlexWrap.WRAP

            val topSpacingDecorator = TopSpacingItemDecoration(0)
            removeItemDecoration(topSpacingDecorator) // does nothing if not applied already
            addItemDecoration(topSpacingDecorator)

            selectedCategoriesValueAdapter =
                SelectedCategoriesValueAdapter(
                )
            addOnScrollListener(object: RecyclerView.OnScrollListener(){

                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                }
            })
            adapter = selectedCategoriesValueAdapter
        }

    }

    private fun saveStoreInformation(){
        if(validPhoneNumber()){
            var selectedCategoriesValue= listOf<String>()
            viewModel.getSelectedCategoriesList()
                .map {
                    selectedCategoriesValue+=it.subCategorys
                }

            viewModel.setStateEvent(
                AccountStateEvent.SetStoreInformation(
                    StoreInformation(
                        -1,
                        input_address.text.toString(),
                        input_phone_number.text.toString(),
                        input_default_phone_number.text.toString(),
                        selectedCategoriesValue,
                        listOf()
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

                        if(!data.response.hasBeenHandled){
                            if (response.message==SuccessHandling.DONE_STORE_INFORMATION){
                                data.data?.let{
                                    it.peekContent()?.let{
                                        it.storeInformationFields!!.storeInformation?.let {
                                            viewModel.setStoreInformation(it)
                                            viewModel.setSelectedCategoriesList(it.defaultCategoriesList!!.toMutableList())
                                        }
                                    }
                                }

                            }
                        }
                    }
                }
            }
        })
        //submit list to recycler view
        viewModel.viewState.observe(viewLifecycleOwner, Observer { viewState ->
            var selectedCategoriesValue= listOf<String>()
            viewModel.getSelectedCategoriesList()
                .map {
                    selectedCategoriesValue+=it.subCategorys
                }

            selectedCategoriesValueAdapter.submitList(selectedCategoriesValue)
            viewModel.getStoreInformation()?.let {
                initUi(it)
            }

        })
    }

    private fun initUi(storeInformation: StoreInformation){
        input_address.setText(storeInformation.address)
        input_default_phone_number.setText(storeInformation.defaultTelephoneNumber)
        input_phone_number.setText(storeInformation.telephoneNumber)
    }

    private fun navAccount(){
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