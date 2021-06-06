package com.smartcity.provider.ui.main.order

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.smartcity.provider.R
import com.smartcity.provider.models.OrderStep
import com.smartcity.provider.models.product.OrderType
import com.smartcity.provider.ui.AreYouSureCallback
import com.smartcity.provider.ui.UIMessage
import com.smartcity.provider.ui.UIMessageType
import com.smartcity.provider.ui.main.order.state.ORDER_VIEW_STATE_BUNDLE_KEY
import com.smartcity.provider.ui.main.order.state.OrderStateEvent
import com.smartcity.provider.ui.main.order.state.OrderViewState
import com.smartcity.provider.ui.main.order.viewmodel.*
import com.smartcity.provider.util.Constants
import com.smartcity.provider.util.DateUtils
import com.smartcity.provider.util.SuccessHandling
import com.smartcity.provider.util.SuccessHandling.Companion.CUSTOM_CATEGORY_UPDATE_DONE
import com.smartcity.provider.util.TopSpacingItemDecoration
import kotlinx.android.synthetic.main.fragment_view_order.*
import kotlinx.android.synthetic.main.layout_order_item_header.view.*
import javax.inject.Inject


class ViewOrderFragment
@Inject
constructor(
    private val viewModelFactory: ViewModelProvider.Factory,
    private val requestManager: RequestManager
): BaseOrderFragment(R.layout.fragment_view_order)
{
    private lateinit var orderProductRecyclerAdapter: OrderProductAdapter
    
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

        setOrderInformation()
        initProductsRecyclerView()
        setProductsList()
        setBillInformation()
        subscribeObservers()
        acceptRejectOrder()
        inProgressOrder()
        readyOrder()
        deliveredPickedUpOrder()
        setButtonsUi()
    }

    private fun setButtonsUi() {
        when(viewModel.getOrderStepFilter()){
            OrderStep.NEW_ORDER ->{
                view_order_NEW_ORDER_buttons.visibility=View.VISIBLE
            }

            OrderStep.ACCEPT_ORDER ->{
                view_order_ACCEPT_ORDER_buttons.visibility=View.VISIBLE
            }

            OrderStep.PROGRESS_ORDER ->{
                view_order_PROGRESS_ORDER_buttons.visibility=View.VISIBLE
            }

            OrderStep.READY_ORDER ->{
                view_order_READY_ORDER_buttons.visibility=View.VISIBLE
            }
        }
    }

    private fun acceptRejectOrder() {
        view_order_accept.setOnClickListener {
            val callback: AreYouSureCallback = object: AreYouSureCallback {
                override fun proceed() {
                    viewModel.setStateEvent(
                        OrderStateEvent.SetOrderAcceptedEvent(
                            viewModel.getSelectedOrder()!!.id
                        )
                    )
                }
                override fun cancel() {
                    // ignore
                }
            }
            uiCommunicationListener.onUIMessageReceived(
                UIMessage(
                    getString(R.string.are_you_sure_accept),
                    UIMessageType.AreYouSureDialog(callback)
                )
            )
        }

        view_order_reject.setOnClickListener {
            val callback: AreYouSureCallback = object: AreYouSureCallback {
                override fun proceed() {
                    viewModel.setStateEvent(
                        OrderStateEvent.SetOrderRejectedEvent(
                            viewModel.getSelectedOrder()!!.id
                        )
                    )
                }
                override fun cancel() {
                    // ignore
                }
            }
            uiCommunicationListener.onUIMessageReceived(
                UIMessage(
                    getString(R.string.are_you_sure_reject),
                    UIMessageType.AreYouSureDialog(callback)
                )
            )
        }
    }

    private fun inProgressOrder() {
        view_order_in_progress.setOnClickListener {
            val callback: AreYouSureCallback = object: AreYouSureCallback {
                override fun proceed() {
                    viewModel.setStateEvent(
                        OrderStateEvent.SetOrderInProgressEvent(
                            viewModel.getSelectedOrder()!!.id
                        )
                    )
                }
                override fun cancel() {
                    // ignore
                }
            }
            uiCommunicationListener.onUIMessageReceived(
                UIMessage(
                    getString(R.string.are_you_sure_in_progress),
                    UIMessageType.AreYouSureDialog(callback)
                )
            )
        }
    }

    private fun readyOrder() {
        view_order_ready.setOnClickListener {
            val callback: AreYouSureCallback = object: AreYouSureCallback {
                override fun proceed() {
                    viewModel.setStateEvent(
                        OrderStateEvent.SetOrderReadyEvent(
                            viewModel.getSelectedOrder()!!.id
                        )
                    )
                }
                override fun cancel() {
                    // ignore
                }
            }
            uiCommunicationListener.onUIMessageReceived(
                UIMessage(
                    getString(R.string.are_you_sure_ready),
                    UIMessageType.AreYouSureDialog(callback)
                )
            )
        }
    }

    private fun deliveredPickedUpOrder() {
        when(viewModel.getSelectedOrder()!!.orderType){
            OrderType.DELIVERY ->{
                view_order_delivered.visibility=View.VISIBLE
            }

            OrderType.SELFPICKUP ->{
                view_order_picked_up.visibility=View.VISIBLE
            }
        }

        view_order_picked_up.setOnClickListener {
            val callback: AreYouSureCallback = object: AreYouSureCallback {
                override fun proceed() {
                    viewModel.setStateEvent(
                        OrderStateEvent.SetOrderPickedUpEvent(
                            viewModel.getSelectedOrder()!!.id
                        )
                    )
                }
                override fun cancel() {
                    // ignore
                }
            }
            uiCommunicationListener.onUIMessageReceived(
                UIMessage(
                    getString(R.string.are_you_sure_picked_up),
                    UIMessageType.AreYouSureDialog(callback)
                )
            )
        }

        view_order_delivered.setOnClickListener {
            val callback: AreYouSureCallback = object: AreYouSureCallback {
                override fun proceed() {
                    viewModel.setStateEvent(
                        OrderStateEvent.SetOrderDeliveredEvent(
                            viewModel.getSelectedOrder()!!.id
                        )
                    )
                }
                override fun cancel() {
                    // ignore
                }
            }
            uiCommunicationListener.onUIMessageReceived(
                UIMessage(
                    getString(R.string.are_you_sure_delivered),
                    UIMessageType.AreYouSureDialog(callback)
                )
            )
        }
    }

    private fun subscribeObservers() {
        viewModel.dataState.observe(viewLifecycleOwner, Observer{ dataState ->
            stateChangeListener.onDataStateChange(dataState)
            if(dataState != null){
                //set Product list get it from network
                dataState.data?.let { data ->
                    data.response?.peekContent()?.let{ response ->
                        when(response.message){

                            CUSTOM_CATEGORY_UPDATE_DONE ->{
                                updateOrderList()

                            }

                            else ->{

                            }

                        }
                    }
                }
            }

        })

    }

    private fun updateOrderList() {
        val list=viewModel.getOrderList().toMutableList()
        list.remove(viewModel.getSelectedOrder())
        viewModel.setOrderListData(list)
        findNavController().popBackStack()
    }

    @SuppressLint("SetTextI18n")
    private fun setOrderInformation() {
        viewModel.getSelectedOrder()?.let {order ->
            view_order_id.text=order.id.toString()

            view_order_time.text= DateUtils.convertStringToStringDate(order.createAt)

            view_order_type_delivery.visibility=View.GONE
            view_order_type_self_pickup.visibility=View.GONE
            when(order.orderType){
                OrderType.DELIVERY ->{
                    view_order_type.text="Delivery"
                    view_order_type_delivery.visibility=View.VISIBLE
                }

                OrderType.SELFPICKUP ->{
                    view_order_type.text="Self pickup"
                    view_order_type_self_pickup.visibility=View.VISIBLE
                    view_order_delivery_address_container.visibility=View.GONE
                }
            }

            view_order_receiver.text="${order.firstName} ${order.lastName} born in ${order.birthDay}"

            order.address?.let {item->
                view_order_delivery_address.text="${item.city}, ${item.street}, ${item.houseNumber.toString()}, ${item.zipCode.toString()}"
            }
        }
    }
    
    fun initProductsRecyclerView(){
        view_order_products_recycler_view.apply {
            layoutManager = LinearLayoutManager(context)
            val topSpacingDecorator = TopSpacingItemDecoration(0)
            removeItemDecoration(topSpacingDecorator) // does nothing if not applied already
            addItemDecoration(topSpacingDecorator)

            orderProductRecyclerAdapter =
                OrderProductAdapter(
                    requestManager
                )
            addOnScrollListener(object: RecyclerView.OnScrollListener(){

                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                }
            })
            adapter = orderProductRecyclerAdapter
        }

    }

    private fun setProductsList() {
        viewModel.getSelectedOrder()?.let { order ->
            orderProductRecyclerAdapter?.let {
                it.submitList(
                    order.orderProductVariants.sortedBy { it.productVariant.price }
                )
            }
        }
    }

    private fun setBillInformation() {
        viewModel.getSelectedOrder()?.let { order ->
            view_order_product_quantity.text= order.orderProductVariants.size.toString()

            view_order_product_total.text=order.bill!!.total.toString()+ Constants.DINAR_ALGERIAN
            view_order_product_paid.text=order.bill!!.alreadyPaid.toString()+ Constants.DINAR_ALGERIAN
            view_order_product_rest.text=(order.bill!!.total-order.bill!!.alreadyPaid).toString()+ Constants.DINAR_ALGERIAN
        }
    }

}