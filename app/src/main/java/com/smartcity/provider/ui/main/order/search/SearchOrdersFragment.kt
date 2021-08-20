package com.smartcity.provider.ui.main.order.search

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.budiyev.android.codescanner.AutoFocusMode
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ScanMode
import com.bumptech.glide.RequestManager
import com.smartcity.provider.R
import com.smartcity.provider.ui.main.order.BaseOrderFragment
import com.smartcity.provider.ui.main.order.state.ORDER_VIEW_STATE_BUNDLE_KEY
import com.smartcity.provider.ui.main.order.state.OrderViewState
import com.smartcity.provider.ui.main.order.viewmodel.OrderViewModel
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
}