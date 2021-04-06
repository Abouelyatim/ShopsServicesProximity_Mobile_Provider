package com.smartcity.provider.ui.main.order


import android.content.SharedPreferences
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.google.firebase.messaging.FirebaseMessaging
import com.smartcity.provider.R
import com.smartcity.provider.ui.main.order.notification.Events
import com.smartcity.provider.ui.main.order.state.ORDER_VIEW_STATE_BUNDLE_KEY
import com.smartcity.provider.ui.main.order.state.OrderStateEvent
import com.smartcity.provider.ui.main.order.state.OrderViewState
import com.smartcity.provider.ui.main.order.viewmodel.OrderViewModel
import com.smartcity.provider.ui.main.order.viewmodel.getOrderList
import com.smartcity.provider.ui.main.order.viewmodel.setOrderListData
import com.smartcity.provider.util.PreferenceKeys
import com.smartcity.provider.util.TopSpacingItemDecoration
import kotlinx.android.synthetic.main.fragment_order.*
import javax.inject.Inject

class OrderFragment
@Inject
constructor(
    private val viewModelFactory: ViewModelProvider.Factory,
    private val requestManager: RequestManager
): BaseOrderFragment(R.layout.fragment_order)
{

    private lateinit var recyclerOrderAdapter: OrderAdapter

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
        viewState?.orderFields?.orderList = ArrayList()

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

        initRecyclerView()
        subscribeObservers()
        getOrders()

    }


    private fun getOrders() {
        viewModel.setStateEvent(
            OrderStateEvent.GetOrderEvent()
        )
    }

    private fun initRecyclerView() {
        orders_recyclerview.apply {
            layoutManager = LinearLayoutManager(this@OrderFragment.context)
            val topSpacingDecorator = TopSpacingItemDecoration(0)
            removeItemDecoration(topSpacingDecorator) // does nothing if not applied already
            addItemDecoration(topSpacingDecorator)

            recyclerOrderAdapter =
                OrderAdapter(
                    requestManager
                )
            addOnScrollListener(object: RecyclerView.OnScrollListener(){

                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                }
            })
            adapter = recyclerOrderAdapter
        }
    }


    private fun subscribeObservers(){
        viewModel.dataState.observe(viewLifecycleOwner, Observer{ dataState ->
            stateChangeListener.onDataStateChange(dataState)

            if(dataState != null){
                //set order list get it from network
                dataState.data?.let { data ->
                    data.data?.let{
                        it.getContentIfNotHandled()?.let{
                            viewModel.setOrderListData(it.orderFields.orderList)
                        }
                    }
                }
            }
        })

        //submit list to recycler view
        viewModel.viewState.observe(viewLifecycleOwner, Observer { viewState ->
            recyclerOrderAdapter.submitList(
                viewModel.getOrderList().asReversed()
            )
        })
        //new order event
        Events.serviceEvent.observe(viewLifecycleOwner, Observer<String> { profile ->
            getOrders()
        })
    }





    override fun onDestroyView() {
        super.onDestroyView()
        // clear references (can leak memory)
        orders_recyclerview.adapter=null
    }



}

















