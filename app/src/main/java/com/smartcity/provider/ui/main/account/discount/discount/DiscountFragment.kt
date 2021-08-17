package com.smartcity.provider.ui.main.account.discount.discount

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.smartcity.provider.R
import com.smartcity.provider.models.Offer
import com.smartcity.provider.models.OfferState
import com.smartcity.provider.ui.main.account.BaseAccountFragment
import com.smartcity.provider.ui.main.account.state.ACCOUNT_VIEW_STATE_BUNDLE_KEY
import com.smartcity.provider.ui.main.account.state.AccountStateEvent
import com.smartcity.provider.ui.main.account.state.AccountViewState
import com.smartcity.provider.ui.main.account.viewmodel.*
import com.smartcity.provider.util.TopSpacingItemDecoration
import kotlinx.android.synthetic.main.fragment_discount.*
import javax.inject.Inject

class DiscountFragment
@Inject
constructor(
    private val viewModelFactory: ViewModelProvider.Factory,
    private val requestManager: RequestManager
): BaseAccountFragment(R.layout.fragment_discount),
    OfferAdapter.Interaction,
    DiscountFilterAdapter.Interaction{

    private lateinit var offerRecyclerAdapter: OfferAdapter

    private lateinit var dialogView: View

    private var statusRecyclerDiscountsAdapter: DiscountFilterAdapter? = null

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

        add_discount_button.setOnClickListener {
            navAddDiscount()
        }
        subscribeObservers()
        getOffers()
        initOfferRecyclerView()
        setOfferFilter()
        viewModel.setSelectedOfferFilter(null)
    }

    private fun setOfferFilter() {
        filter_button.setOnClickListener {
            showFilterDialog()
        }
    }

    private fun initFilterDiscountRecyclerView(recyclerView: RecyclerView,recyclerAdapter:DiscountFilterAdapter) {
        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@DiscountFragment.context,LinearLayoutManager.HORIZONTAL,false)
            addOnScrollListener(object: RecyclerView.OnScrollListener(){

                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val lastPosition = layoutManager.findLastVisibleItemPosition()

                }
            })
            adapter = recyclerAdapter
        }
    }

    private val statusFilter = listOf(
        Pair("Active", OfferState.ACTIVE),
        Pair("Expired",OfferState.EXPIRED),
        Pair("Planned",OfferState.PLANNED)
    )

    private fun showFilterDialog(){
        val dialog = BottomSheetDialog(requireContext(),R.style.BottomSheetDialogTheme)
        dialogView = layoutInflater.inflate(R.layout.dialog_filter_dscount, null)
        dialog.setCancelable(true)
        dialog.setContentView(dialogView)

        val statusRecyclerView = dialogView.findViewById<RecyclerView>(R.id.filter_type_discounts)
        statusRecyclerDiscountsAdapter = DiscountFilterAdapter(this@DiscountFragment)
        initFilterDiscountRecyclerView(statusRecyclerView,statusRecyclerDiscountsAdapter!!)
        statusRecyclerDiscountsAdapter!!.submitList(
            statusFilter.map { it.first },
            if (viewModel.getSelectedOfferFilter() == null) "" else viewModel.getSelectedOfferFilter()!!.first
        )
        Log.d("ii",viewModel.getSelectedOfferFilter().toString())
        val viewDiscountsButton = dialogView.findViewById<Button>(R.id.view_discounts_button)
        viewDiscountsButton.setOnClickListener {
            getOffers()
            dialog.dismiss()
        }
        dialog.show()
    }

    override fun onItemSelected(item: String) {
        viewModel.setSelectedOfferFilter(statusFilter.find { it.first == item })
        statusRecyclerDiscountsAdapter!!.notifyDataSetChanged()
    }

    override fun onItemDeSelected(item: String) {
        viewModel.setSelectedOfferFilter(null)
        statusRecyclerDiscountsAdapter!!.notifyDataSetChanged()
    }

    private fun getOffers() {
        viewModel.setStateEvent(
            AccountStateEvent.GetOffersEvent()
        )
    }

    private fun subscribeObservers() {
        viewModel.dataState.observe(viewLifecycleOwner, Observer { dataState ->
            stateChangeListener.onDataStateChange(dataState)
            //set Offer list get it from network
            dataState.data?.let { data ->
                data.data?.let{
                    it.getContentIfNotHandled()?.let{
                        it.discountOfferList.offersList.let {
                            viewModel.setOffersList(it)
                        }
                    }

                }

            }
        })

        //submit list to recycler view
        viewModel.viewState.observe(viewLifecycleOwner, Observer { viewState ->
            offerRecyclerAdapter.submitList(viewModel.getOffersList())

            statusRecyclerDiscountsAdapter?.submitList(
                statusFilter.map { it.first },
                if (viewModel.getSelectedOfferFilter() == null) "" else viewModel.getSelectedOfferFilter()!!.first
            )

        })
    }


    fun navAddDiscount(){
        findNavController().navigate(R.id.action_discountFragment_to_addDiscountFragment)
    }

    override fun onResume() {
        super.onResume()
        viewModel.clearDiscountFields()
        viewModel.setSelectedOffer(null)
    }

    private fun initOfferRecyclerView(){
        offer_recyclerview.apply {
            layoutManager = LinearLayoutManager(this@DiscountFragment.context)
            val topSpacingDecorator = TopSpacingItemDecoration(0)
            removeItemDecoration(topSpacingDecorator) // does nothing if not applied already
            addItemDecoration(topSpacingDecorator)

            offerRecyclerAdapter =
                OfferAdapter(
                    this@DiscountFragment
                )
            addOnScrollListener(object: RecyclerView.OnScrollListener(){

                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                }
            })
            adapter = offerRecyclerAdapter
        }
    }

    override fun onItemSelected(item: Offer) {
        viewModel.setSelectedOffer(item)
        findNavController().navigate(R.id.action_discountFragment_to_viewOfferFragment)
    }
}