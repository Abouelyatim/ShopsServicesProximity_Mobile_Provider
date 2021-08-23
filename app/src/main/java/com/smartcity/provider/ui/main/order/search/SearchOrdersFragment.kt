package com.smartcity.provider.ui.main.order.search

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.RequestManager
import com.smartcity.provider.R
import com.smartcity.provider.ui.main.order.BaseOrderFragment
import com.smartcity.provider.ui.main.order.state.ORDER_VIEW_STATE_BUNDLE_KEY
import com.smartcity.provider.ui.main.order.state.OrderStateEvent
import com.smartcity.provider.ui.main.order.state.OrderViewState
import com.smartcity.provider.ui.main.order.viewmodel.OrderViewModel
import com.smartcity.provider.ui.main.order.viewmodel.setSearchOrderListData
import com.smartcity.provider.util.SuccessHandling
import kotlinx.android.synthetic.main.fragment_search_orders.*
import javax.inject.Inject


class SearchOrdersFragment
@Inject
constructor(
    private val viewModelFactory: ViewModelProvider.Factory,
    private val requestManager: RequestManager
): BaseOrderFragment(R.layout.fragment_search_orders){

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

        scanQrCode()
        searchByReceiver()
        searchByDate()
        searchPastOrders()
        subscribeObservers()
    }

    private fun searchByDate() {
        search_order_date.setOnClickListener {
            findNavController().navigate(R.id.action_searchOrdersFragment_to_pickDateFragment)
        }
    }

    private fun searchByReceiver() {
        search_order_receiver_name.setOnClickListener {
            findNavController().navigate(R.id.action_searchOrdersFragment_to_receiverNameFragment)
        }
    }

    private fun scanQrCode() {
        search_order_scan_qr_code.setOnClickListener {
            if(stateChangeListener.isCameraPermissionGranted()){
                findNavController().navigate(R.id.action_searchOrdersFragment_to_scanQrCodeFragment)
            }else{
                stateChangeListener.isCameraPermissionGranted()
            }
        }
    }

    private fun searchPastOrders(){
        search_past_orders.setOnClickListener {
            viewModel.setStateEvent(
                OrderStateEvent.GetPastOrderEvent()
            )
        }
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
                                        findNavController().navigate(R.id.action_searchOrdersFragment_to_viewSearchOrdersFragment)
                                    }
                                }

                            }
                        }
                    }
                }
            }
        })
    }
}