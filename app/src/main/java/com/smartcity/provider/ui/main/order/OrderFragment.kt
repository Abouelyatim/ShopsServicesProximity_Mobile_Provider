package com.smartcity.provider.ui.main.order


import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import com.smartcity.provider.R
import com.smartcity.provider.ui.main.order.OrderActionAdapter.Companion.getSelectedPositions
import com.smartcity.provider.ui.main.order.OrderActionAdapter.Companion.setSelectedPositions
import com.smartcity.provider.ui.main.order.OrderFragment.ActionOrder.ALL
import com.smartcity.provider.ui.main.order.OrderFragment.ActionOrder.DATE
import com.smartcity.provider.ui.main.order.OrderFragment.ActionOrder.TODAY
import com.smartcity.provider.ui.main.order.notification.Events
import com.smartcity.provider.ui.main.order.state.ORDER_VIEW_STATE_BUNDLE_KEY
import com.smartcity.provider.ui.main.order.state.OrderStateEvent
import com.smartcity.provider.ui.main.order.state.OrderViewState
import com.smartcity.provider.ui.main.order.viewmodel.*
import com.smartcity.provider.util.DateUtils.Companion.convertLongToStringDate
import com.smartcity.provider.util.RightSpacingItemDecoration
import com.smartcity.provider.util.TopSpacingItemDecoration
import kotlinx.android.synthetic.main.fragment_order.*
import javax.inject.Inject


class OrderFragment
@Inject
constructor(
    private val viewModelFactory: ViewModelProvider.Factory,
    private val requestManager: RequestManager
): BaseOrderFragment(R.layout.fragment_order),
    OrderActionAdapter.Interaction
{
    object ActionOrder {
        val ALL = Pair<String,Int>("All orders",0)
        val TODAY = Pair<String,Int>("Today",1)
        val DATE = Pair<String,Int>("Record",2)
    }

    private lateinit var recyclerOrderActionAdapter: OrderActionAdapter
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

        initOrderRecyclerView()
        initOrderActionRecyclerView()
        setOrderAction()
        subscribeObservers()
        initData(viewModel.getOrderActionRecyclerPosition())

    }

    private fun initData(position: Int){
        when(position){
            ALL.second ->{
                getAllOrders()
                setDateRangeUi(false)
            }

            TODAY.second ->{
                getTodayOrders()
                setDateRangeUi(false)
            }

            DATE.second ->{
                setDateRangeUi(true)
            }
        }

        order_date_range.setOnClickListener {
            showDatePicker()
        }
    }

    private fun getAllOrders() {
        viewModel.setStateEvent(
            OrderStateEvent.GetOrderEvent()
        )
    }

    private fun initOrderRecyclerView() {
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

    fun initOrderActionRecyclerView(){
        order_action_recyclerview.apply {
            layoutManager = LinearLayoutManager(this@OrderFragment.context,LinearLayoutManager.HORIZONTAL, false)

            val rightSpacingDecorator = RightSpacingItemDecoration(16)
            removeItemDecoration(rightSpacingDecorator) // does nothing if not applied already
            addItemDecoration(rightSpacingDecorator)

            recyclerOrderActionAdapter =
                OrderActionAdapter(
                    this@OrderFragment
                )
            addOnScrollListener(object: RecyclerView.OnScrollListener(){

                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val lastPosition = layoutManager.findLastVisibleItemPosition()

                }
            })
            recyclerOrderActionAdapter.stateRestorationPolicy= RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
            adapter = recyclerOrderActionAdapter
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
                            setEmptyListUi(it.orderFields.orderList.isEmpty())
                        }
                    }
                }
            }
        })
        //submit list to recycler view
        viewModel.viewState.observe(viewLifecycleOwner, Observer { viewState ->
            recyclerOrderAdapter.submitList(
                viewModel.getOrderList()
            )
        })
        //new order event
        Events.serviceEvent.observe(viewLifecycleOwner, Observer<String> { profile ->
            if(getSelectedPositions()== TODAY.second){
                getTodayOrders()
            }
        })

        recyclerOrderActionAdapter.apply {
            submitList(
                viewModel.getOrderAction()
            )
        }
    }

    private fun setEmptyListUi(empty:Boolean){
        if(empty){
            empty_list.visibility=View.VISIBLE
        }else{
            empty_list.visibility=View.GONE
        }
    }

    private fun setOrderAction(){
        val list= mutableListOf<Triple<String,Int,Int>>()
        list.add(ALL.second,Triple(ALL.first,R.drawable.ic_baseline_all_inclusive_white,R.drawable.ic_baseline_all_inclusive_black))
        list.add(TODAY.second,Triple(TODAY.first,R.drawable.ic_baseline_today_white,R.drawable.ic_baseline_today_black))
        list.add(DATE.second,Triple(DATE.first,R.drawable.ic_baseline_history_white,R.drawable.ic_baseline_history_black))
        viewModel.setOrderActionList(
            list
        )
    }

    override fun onItemSelected(position: Int, item: String) {
        order_action_recyclerview.adapter!!.notifyDataSetChanged()
        resetUI()
        viewModel.clearOrderList()
        when(item){
            ALL.first->{
                getAllOrders()
                setDateRangeUi(false)
            }

            TODAY.first->{
                getTodayOrders()
                setDateRangeUi(false)
            }

            DATE.first->{
                showDatePicker()
                setDateRangeUi(true)
            }
        }
    }

    private fun showDatePicker(){
        val calendarConstraints= CalendarConstraints.Builder()
        calendarConstraints.setValidator(DateValidatorPointBackward.now())

       val builder= MaterialDatePicker.Builder.dateRangePicker()
        builder.setTitleText(R.string.select_date)
        builder.setTheme(R.style.CustomThemeOverlay_MaterialCalendar_Fullscreen)
        builder.setCalendarConstraints(calendarConstraints.build())
       // builder.setSelection()
        val materialDatePicker=builder.build()

        materialDatePicker.addOnPositiveButtonClickListener {
            getOrdersByDate(
                convertLongToStringDate(it.first!!),
                convertLongToStringDate(it.second!!)
            )
        }
        materialDatePicker.show(activity!!.supportFragmentManager,"DATE_PICKER")
    }

    private fun getTodayOrders() {
        viewModel.setStateEvent(
            OrderStateEvent.GetTodayOrderEvent()
        )
    }

    private fun getOrdersByDate(startDate:String,endDate:String) {
        viewModel.setStateEvent(
            OrderStateEvent.GetOrderByDateEvent(
                startDate,
                endDate
            )
        )
    }

    private  fun resetUI(){
        orders_recyclerview.smoothScrollToPosition(0)
        stateChangeListener.hideSoftKeyboard()
        focusable_view.requestFocus()
    }

    private fun setDateRangeUi(visibility: Boolean){
        if (visibility)
            order_date_range.visibility=View.VISIBLE
        else
            order_date_range.visibility=View.GONE
    }

    override fun onResume() {
        super.onResume()
        setSelectedPositions(viewModel.getOrderActionRecyclerPosition())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // clear references (can leak memory)
        viewModel.setOrderActionRecyclerPosition(getSelectedPositions())
        recyclerOrderActionAdapter.resetSelectedPosition()
        orders_recyclerview.adapter=null
        order_action_recyclerview.adapter=null
    }
}

















