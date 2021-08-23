package com.smartcity.provider.ui.main.order


import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.RequestManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import com.smartcity.provider.R
import com.smartcity.provider.models.OrderStep
import com.smartcity.provider.models.product.Order
import com.smartcity.provider.ui.main.order.OrderActionAdapter.Companion.getSelectedActionPositions
import com.smartcity.provider.ui.main.order.OrderActionAdapter.Companion.setSelectedActionPositions
import com.smartcity.provider.ui.main.order.OrderFragment.ActionOrder.ALL
import com.smartcity.provider.ui.main.order.OrderFragment.ActionOrder.DATE
import com.smartcity.provider.ui.main.order.OrderFragment.ActionOrder.TODAY
import com.smartcity.provider.ui.main.order.OrderFragment.StepsOrder.ACCEPT
import com.smartcity.provider.ui.main.order.OrderFragment.StepsOrder.CONFIRMATION
import com.smartcity.provider.ui.main.order.OrderFragment.StepsOrder.NEW
import com.smartcity.provider.ui.main.order.OrderFragment.StepsOrder.PROBLEM
import com.smartcity.provider.ui.main.order.OrderFragment.StepsOrder.READY
import com.smartcity.provider.ui.main.order.OrderStepsAdapter.Companion.getSelectedStepPositions
import com.smartcity.provider.ui.main.order.OrderStepsAdapter.Companion.setSelectedStepPositions
import com.smartcity.provider.ui.main.order.notification.Events
import com.smartcity.provider.ui.main.order.state.ORDER_VIEW_STATE_BUNDLE_KEY
import com.smartcity.provider.ui.main.order.state.OrderStateEvent
import com.smartcity.provider.ui.main.order.state.OrderViewState
import com.smartcity.provider.ui.main.order.viewmodel.*
import com.smartcity.provider.util.DateUtils.Companion.convertDatePickerStringDateToLong
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
    OrderActionAdapter.Interaction,
    OrderAdapter.Interaction,
    OrderStepsAdapter.Interaction,
    SwipeRefreshLayout.OnRefreshListener,
    OrderFilterAdapter.Interaction
{
    object ActionOrder {
        val ALL = Pair<String,Int>("All orders",0)
        val TODAY = Pair<String,Int>("Today",1)
        val DATE = Pair<String,Int>("Record",2)
    }

    private lateinit var recyclerOrderStepsAdapter: OrderStepsAdapter
    private lateinit var recyclerOrderActionAdapter: OrderActionAdapter
    private lateinit var recyclerOrderAdapter: OrderAdapter

    private var sortRecyclerOrdersAdapter: OrderFilterAdapter? = null
    private var typeRecyclerOrdersAdapter: OrderFilterAdapter? = null
    private lateinit var dialogView: View

    object StepsOrder {
        val NEW = Pair<String,Int>("new",0)
        val ACCEPT= Pair<String,Int>("accept",1)
        val READY = Pair<String,Int>("ready",2)
        val CONFIRMATION = Pair<String,Int>("waiting confirmation",3)
        val PROBLEM = Pair<String,Int>("problem",4)
    }


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
        swipe_refresh.setOnRefreshListener(this)

        initOrderRecyclerView()
        initOrderActionRecyclerView()
        initOrderStepsRecyclerView()

        setOrderAction()
        setOrderSteps()

        subscribeObservers()
        initData(viewModel.getOrderActionRecyclerPosition(),viewModel.getOrderStepsRecyclerPosition())

        setEmptyListUi(viewModel.getOrderList().isEmpty())
    }

     fun initData(actionPosition: Int,stepPosition: Int){
        when(stepPosition){
            NEW.second ->{
                viewModel.setOrderStepFilter(OrderStep.NEW_ORDER)
            }

            ACCEPT.second ->{
                viewModel.setOrderStepFilter(OrderStep.ACCEPT_ORDER)
            }

            READY.second ->{
                viewModel.setOrderStepFilter(OrderStep.READY_ORDER)
            }

            CONFIRMATION.second ->{
                viewModel.setOrderStepFilter(OrderStep.CONFIRMATION_ORDER)
            }
        }

        when(actionPosition){
            ALL.second ->{
                getAllOrders()
                setDateRangeUi(false)
            }

            TODAY.second ->{
                getTodayOrders()
                setDateRangeUi(false)
            }

            DATE.second ->{
                getOrdersByDate()
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
                    requestManager,
                    this@OrderFragment
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

    fun initOrderStepsRecyclerView(){
        order_steps_recyclerview.apply {

            this.layoutManager =GridLayoutManager(this@OrderFragment.context, 5, GridLayoutManager.HORIZONTAL, false)

            val rightSpacingDecorator = RightSpacingItemDecoration(0)
            removeItemDecoration(rightSpacingDecorator) // does nothing if not applied already
            addItemDecoration(rightSpacingDecorator)

            recyclerOrderStepsAdapter =
                OrderStepsAdapter(
                    this@OrderFragment
                )
            addOnScrollListener(object: RecyclerView.OnScrollListener(){

                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val lastPosition = layoutManager.findLastVisibleItemPosition()

                }
            })
            recyclerOrderStepsAdapter.stateRestorationPolicy= RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
            adapter = recyclerOrderStepsAdapter
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

            sortRecyclerOrdersAdapter?.submitList(
                sortFilter.map { it.first },
                if (viewModel.getSelectedSortFilter() == null) "" else viewModel.getSelectedSortFilter()!!.first
            )

            typeRecyclerOrdersAdapter?.submitList(
                typeFilter.map { it.first },
                if (viewModel.getSelectedTypeFilter() == null) "" else viewModel.getSelectedTypeFilter()!!.first
            )
        })
        //new order event
        Events.serviceEvent.observe(viewLifecycleOwner, Observer<String> { profile ->
            if(getSelectedActionPositions()== TODAY.second){
                getTodayOrders()
            }
        })

        recyclerOrderActionAdapter.apply {
            submitList(
                viewModel.getOrderAction()
            )
        }

        recyclerOrderStepsAdapter.apply {
            submitList(
                viewModel.getOrderSteps()
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

    private fun setOrderSteps(){
        val list= mutableListOf<Pair<String,Int>>()
        list.add(NEW.second,Pair(NEW.first,R.drawable.ic_baseline_list_alt_24a))
        list.add(ACCEPT.second,Pair(ACCEPT.first,R.drawable.ic_outline_fact_check_24))
        list.add(READY.second,Pair(READY.first,R.drawable.ic_outline_shopping_bag_24))
        list.add(CONFIRMATION.second,Pair(CONFIRMATION.first,R.drawable.ic_baseline_check_24))
        list.add(PROBLEM.second,Pair(PROBLEM.first,R.drawable.ic_outline_report_problem_24))
        viewModel.setOrderStepsList(
            list
        )
    }

    override fun onActionItemSelected(position: Int, item: String) {
        order_action_recyclerview.adapter!!.notifyDataSetChanged()
        resetUI()
        viewModel.setOrderActionRecyclerPosition(position)
        viewModel.clearOrderList()

        viewModel.setSelectedSortFilter(null)
        viewModel.setSelectedTypeFilter(null)
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

    override fun onStepItemSelected(position: Int, item: String) {
        order_steps_recyclerview.adapter!!.notifyDataSetChanged()
        resetUI()
        viewModel.setOrderStepsRecyclerPosition(position)
        viewModel.clearOrderList()

        viewModel.setSelectedSortFilter(null)
        viewModel.setSelectedTypeFilter(null)
        when(item){
            NEW.first ->{
                viewModel.setOrderStepFilter(OrderStep.NEW_ORDER)
            }

            ACCEPT.first ->{
                viewModel.setOrderStepFilter(OrderStep.ACCEPT_ORDER)
            }

            READY.first ->{
                viewModel.setOrderStepFilter(OrderStep.READY_ORDER)
            }

            CONFIRMATION.first ->{
                viewModel.setOrderStepFilter(OrderStep.CONFIRMATION_ORDER)
            }
        }

        when(getSelectedActionPositions()){
            ALL.second->{
                getAllOrders()
                setDateRangeUi(false)
            }

            TODAY.second->{
                getTodayOrders()
                setDateRangeUi(false)
            }

            DATE.second->{
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
        builder.setTheme(R.style.CustomThemeOverlay_MaterialCalendar)
        builder.setCalendarConstraints(calendarConstraints.build())

        if (!viewModel.getRangeDate().first.isNullOrEmpty() && !viewModel.getRangeDate().second.isNullOrEmpty()){
            builder.setSelection(androidx.core.util.Pair(
                convertDatePickerStringDateToLong(viewModel.getRangeDate().first!!),
                convertDatePickerStringDateToLong(viewModel.getRangeDate().second!!)
            ))
        }

        val materialDatePicker=builder.build()

        materialDatePicker.addOnPositiveButtonClickListener {
            viewModel.setRangeDate(
                Pair(
                    convertLongToStringDate(it.first!!),
                    convertLongToStringDate(it.second!!)
                )
            )

            getOrdersByDate()
        }
        materialDatePicker.show(activity!!.supportFragmentManager,"DATE_PICKER")
    }

    private fun getTodayOrders() {
        viewModel.setStateEvent(
            OrderStateEvent.GetTodayOrderEvent()
        )
    }

    private fun getOrdersByDate() {
        viewModel.setStateEvent(
            OrderStateEvent.GetOrderByDateEvent()
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

    private fun navSearch(){
        findNavController().navigate(R.id.action_orderFragment_to_searchOrdersFragment)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.search_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId){
            R.id.action_filter_settings -> {
                showFilterDialog()
                return true
            }

            R.id.action_search_settings -> {
                navSearch()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initFilterOrderRecyclerView(recyclerView: RecyclerView,recyclerAdapter:OrderFilterAdapter) {
        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@OrderFragment.context,LinearLayoutManager.HORIZONTAL,false)

            val rightSpacingDecorator = RightSpacingItemDecoration(0)
            removeItemDecoration(rightSpacingDecorator) // does nothing if not applied already
            addItemDecoration(rightSpacingDecorator)

            addOnScrollListener(object: RecyclerView.OnScrollListener(){

                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val lastPosition = layoutManager.findLastVisibleItemPosition()

                }
            })
            recyclerOrderActionAdapter.stateRestorationPolicy= RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
            adapter = recyclerAdapter
        }
    }

    private val sortFilter= listOf(
        Triple("Date : Oldest first","date","ASC"),
        Triple("Date : Recent first","date","DESC"),
        Triple("Total : Lowest first","amount","ASC"),
        Triple("Total : Highest first","amount","DESC"),
    )

    private val typeFilter = listOf(
        Triple("Pick up","type","SELFPICKUP"),
        Triple("delivery","type","DELIVERY")
    )

    private fun showFilterDialog() {
        val dialog = BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme)
        dialogView = layoutInflater.inflate(R.layout.dialog_filter_order, null)
        dialog.setCancelable(true)
        dialog.setContentView(dialogView)

        val sortRecyclerView = dialogView.findViewById<RecyclerView>(R.id.filter_sort_orders)
        sortRecyclerOrdersAdapter = OrderFilterAdapter(this@OrderFragment)
        initFilterOrderRecyclerView(sortRecyclerView, sortRecyclerOrdersAdapter!!)
        sortRecyclerOrdersAdapter!!.submitList(
            sortFilter.map { it.first },
            if (viewModel.getSelectedSortFilter() == null) "" else viewModel.getSelectedSortFilter()!!.first
        )

        val typeRecyclerView = dialogView.findViewById<RecyclerView>(R.id.filter_type_orders)
        typeRecyclerOrdersAdapter = OrderFilterAdapter(this@OrderFragment)
        initFilterOrderRecyclerView(typeRecyclerView, typeRecyclerOrdersAdapter!!)
        typeRecyclerOrdersAdapter!!.submitList(
            typeFilter.map { it.first },
            if (viewModel.getSelectedTypeFilter() == null) "" else viewModel.getSelectedTypeFilter()!!.first
        )

        val viewOrdersButton = dialogView.findViewById<Button>(R.id.view_orders_button)
        viewOrdersButton.setOnClickListener {
            resetUI()
            initData(getSelectedActionPositions(), getSelectedStepPositions())
            dialog.dismiss()
        }
        dialog.show()
    }

    override fun onItemSelected(item: String) {
        if(item in sortFilter.map { it.first }){
            viewModel.setSelectedSortFilter(sortFilter.find { it.first == item }!!)
            sortRecyclerOrdersAdapter!!.notifyDataSetChanged()
        }
        if(item in typeFilter.map { it.first }){
            viewModel.setSelectedTypeFilter(typeFilter.find { it.first == item }!!)
            typeRecyclerOrdersAdapter!!.notifyDataSetChanged()
        }
    }

    override fun onItemDeSelected(item: String) {
        if(item in sortFilter.map { it.first }){
            viewModel.setSelectedSortFilter(null)
            sortRecyclerOrdersAdapter!!.notifyDataSetChanged()
        }
        if(item in typeFilter.map { it.first }){
            viewModel.setSelectedTypeFilter(null)
            typeRecyclerOrdersAdapter!!.notifyDataSetChanged()
        }
    }

    override fun onResume() {
        super.onResume()
        setSelectedActionPositions(viewModel.getOrderActionRecyclerPosition())
        setSelectedStepPositions(viewModel.getOrderStepsRecyclerPosition())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // clear references (can leak memory)
        viewModel.setOrderActionRecyclerPosition(getSelectedActionPositions())
        viewModel.setOrderStepsRecyclerPosition(getSelectedStepPositions())
        recyclerOrderActionAdapter.resetSelectedActionPosition()
        recyclerOrderStepsAdapter.resetSelectedStepPosition()
        orders_recyclerview.adapter=null
        order_action_recyclerview.adapter=null
        order_steps_recyclerview.adapter=null
    }

    override fun onRefresh() {
        initData(viewModel.getOrderActionRecyclerPosition(), viewModel.getOrderStepsRecyclerPosition())
        swipe_refresh.isRefreshing = false
    }

    override fun selectedOrder(item: Order) {
        viewModel.setSelectedOrder(item)
        navViewOrder()
    }

    private fun navViewOrder(){
        findNavController().navigate(R.id.action_orderFragment_to_viewOrderFragment)
    }
}

















