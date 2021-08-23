package com.smartcity.provider.ui.main.order

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.RequestManager
import com.smartcity.provider.R
import com.smartcity.provider.ui.main.order.state.ORDER_VIEW_STATE_BUNDLE_KEY
import com.smartcity.provider.ui.main.order.state.OrderStateEvent
import com.smartcity.provider.ui.main.order.state.OrderViewState
import com.smartcity.provider.ui.main.order.viewmodel.OrderViewModel
import com.smartcity.provider.ui.main.order.viewmodel.getSelectedOrder
import com.smartcity.provider.ui.main.order.viewmodel.setOrderListData
import com.smartcity.provider.ui.main.order.viewmodel.setSelectedOrder
import com.smartcity.provider.util.SuccessHandling
import kotlinx.android.synthetic.main.fragment_add_order_note.*
import javax.inject.Inject


class AddOrderNoteFragment
@Inject
constructor(
    private val viewModelFactory: ViewModelProvider.Factory,
    private val requestManager: RequestManager
): BaseOrderFragment(R.layout.fragment_add_order_note)
{
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
        // viewState?.orderFields?.orderList = ArrayList()

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
        save_order_note_button.setOnClickListener {
            saveOrderNote(
                input_order_note.text.toString()
            )
        }
    }

    private fun subscribeObservers() {
        viewModel.dataState.observe(viewLifecycleOwner, Observer { dataState ->
            stateChangeListener.onDataStateChange(dataState)

            if(dataState != null){
                //set Product list get it from network
                dataState.data?.let { data ->
                    data.response?.peekContent()?.let{ response ->
                        if(response.message == SuccessHandling.CREATION_DONE){
                            val order = viewModel.getSelectedOrder()
                            order!!.providerNote = input_order_note.text.toString()
                            viewModel.setSelectedOrder(order)
                            findNavController().popBackStack()
                        }
                    }
                }
            }
        })
    }

    private fun saveOrderNote(note :String){
        viewModel.setStateEvent(
            OrderStateEvent.SetOrderNoteEvent(
                id =  viewModel.getSelectedOrder()!!.id,
                note = note
            )
        )
    }
}