package com.smartcity.provider.ui.main.order.search

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.budiyev.android.codescanner.CodeScanner
import com.bumptech.glide.RequestManager
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.smartcity.provider.R
import com.smartcity.provider.ui.main.order.BaseOrderFragment
import com.smartcity.provider.ui.main.order.state.ORDER_VIEW_STATE_BUNDLE_KEY
import com.smartcity.provider.ui.main.order.state.OrderStateEvent
import com.smartcity.provider.ui.main.order.state.OrderViewState
import com.smartcity.provider.ui.main.order.viewmodel.OrderViewModel
import com.smartcity.provider.ui.main.order.viewmodel.setSearchOrderListData
import com.smartcity.provider.util.DateUtils
import com.smartcity.provider.util.SuccessHandling
import kotlinx.android.synthetic.main.fragment_pick_date.*
import javax.inject.Inject


class PickDateFragment
@Inject
constructor(
    private val viewModelFactory: ViewModelProvider.Factory,
    private val requestManager: RequestManager
): BaseOrderFragment(R.layout.fragment_pick_date){

    val viewModel: OrderViewModel by viewModels{
        viewModelFactory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cancelActiveJobs()
        // Restore state after process death
        savedInstanceState?.let { inState ->
            (inState[ORDER_VIEW_STATE_BUNDLE_KEY] as OrderViewState?)?.let { viewState ->
                viewModel.setViewState(viewState)
            }
        }
    }

    /**
     * !IMPORTANT!
     * Must save ViewState b/c in event of process death the LiveData in ViewModel will be lost
     */
    override fun onSaveInstanceState(outState: Bundle) {
        val viewState = viewModel.viewState.value

        //clear the list. Don't want to save a large list to bundle.
        //viewState?.orderFields?.orderList = ArrayList()

        outState.putParcelable(
            ORDER_VIEW_STATE_BUNDLE_KEY,
            viewState
        )
        super.onSaveInstanceState(outState)
    }

    override fun cancelActiveJobs(){
        viewModel.cancelActiveJobs()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(false)
        setHasOptionsMenu(true)


        subscribeObservers()
        pickDate()
        searchOrders()
    }

    private fun searchOrders() {
        search_orders_button.setOnClickListener {
            if(search_orders_date_text.text.toString() != "xxxx-xx-xx"){
                searchOrdersByDate(search_orders_date_text.text.toString())
            }
        }
    }

    private fun searchOrdersByDate(date:String){
        viewModel.setStateEvent(
            OrderStateEvent.SearchOrderByDateEvent(
                date
            )
        )
    }

    private fun pickDate() {
        pick_search_orders_date.setOnClickListener {
            showDatePicker()
        }
    }

    private fun showDatePicker() {
        val calendarConstraints= CalendarConstraints.Builder()
        calendarConstraints.setValidator(DateValidatorPointBackward.now())

        val builder= MaterialDatePicker.Builder.datePicker()
        builder.setTitleText(R.string.select_date)
        builder.setTheme(R.style.CustomThemeOverlay_MaterialCalendar)
        builder.setCalendarConstraints(calendarConstraints.build())

        val materialDatePicker=builder.build()
        materialDatePicker.addOnPositiveButtonClickListener {
            search_orders_date_text.text = DateUtils.convertLongToStringDate(it)
        }
        materialDatePicker.show(activity!!.supportFragmentManager,"DATE_PICKER")
    }

    private fun subscribeObservers() {
        viewModel.dataState.observe(viewLifecycleOwner, Observer{ dataState ->
            stateChangeListener.onDataStateChange(dataState)

            if(dataState != null){
                //set order list get it from network
                dataState.data?.let { data ->
                    data.response?.peekContent()?.let{ response ->
                        if(response.message == SuccessHandling.DONE_Order){
                            data.data?.let{
                                it.peekContent()?.let{
                                    it.orderFields.searchOrderList.let {
                                        viewModel.setSearchOrderListData(it)
                                        navViewSearch()
                                    }
                                }

                            }
                        }
                    }
                }
            }
        })
    }

    private fun navViewSearch() {
        findNavController().navigate(R.id.action_pickDateFragment_to_viewSearchOrdersFragment)
    }
}