package com.smartcity.provider.ui.main.custom_category.product

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.RequestManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.smartcity.provider.R
import com.smartcity.provider.models.product.Product
import com.smartcity.provider.ui.AreYouSureCallback
import com.smartcity.provider.ui.UIMessage
import com.smartcity.provider.ui.UIMessageType
import com.smartcity.provider.ui.main.custom_category.BaseCustomCategoryFragment
import com.smartcity.provider.ui.main.custom_category.viewmodel.CustomCategoryViewModel
import com.smartcity.provider.ui.main.custom_category.state.CUSTOM_CATEGORY_VIEW_STATE_BUNDLE_KEY
import com.smartcity.provider.ui.main.custom_category.state.CustomCategoryStateEvent
import com.smartcity.provider.ui.main.custom_category.state.CustomCategoryViewState
import com.smartcity.provider.ui.main.order.OrderFilterAdapter
import com.smartcity.provider.util.ActionConstants
import com.smartcity.provider.util.RightSpacingItemDecoration
import com.smartcity.provider.util.SuccessHandling
import com.smartcity.provider.util.SuccessHandling.Companion.CUSTOM_CATEGORY_UPDATE_DONE
import com.smartcity.provider.util.TopSpacingItemDecoration
import kotlinx.android.synthetic.main.fragment_product.*
import javax.inject.Inject


class ProductFragment
@Inject
constructor(
    private val viewModelFactory: ViewModelProvider.Factory,
    private val requestManager: RequestManager
): BaseCustomCategoryFragment(R.layout.fragment_product),
    SwipeRefreshLayout.OnRefreshListener,
    ProductAdapter.Interaction,
    OrderFilterAdapter.Interaction
{

    private lateinit var productRecyclerAdapter: ProductAdapter

    private var categoryProductAdapter: OrderFilterAdapter? = null
    private lateinit var dialogView: View

    val viewModel: CustomCategoryViewModel by viewModels{
        viewModelFactory
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cancelActiveJobs()
        // Restore state after process death
        savedInstanceState?.let { inState ->
            (inState[CUSTOM_CATEGORY_VIEW_STATE_BUNDLE_KEY] as CustomCategoryViewState?)?.let { viewState ->
                viewModel.setViewState(viewState)
            }
        }
    }
    override fun onSaveInstanceState(outState: Bundle) {
        outState.putParcelable(
            CUSTOM_CATEGORY_VIEW_STATE_BUNDLE_KEY,
            viewModel.viewState.value
        )
        super.onSaveInstanceState(outState)
    }
    override fun cancelActiveJobs(){
        viewModel.cancelActiveJobs()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        swipe_refresh.setOnRefreshListener(this)
        stateChangeListener.expandAppBar()

        addProduct()
        initProductRecyclerView()
        ProductMain()
        subscribeObservers()
        setToolBareText(viewModel.getSelectedCustomCategory()!!.name)
    }

    private fun setToolBareText(text:String){
        toolbarTitle.text=text
    }

    fun ProductMain(){
        viewModel.setStateEvent(
            CustomCategoryStateEvent.ProductMainEvent(
            viewModel.getSelectedCustomCategory()!!.pk.toLong()
        ))
    }



    private fun subscribeObservers() {
        viewModel.dataState.observe(viewLifecycleOwner, Observer { dataState ->
            stateChangeListener.onDataStateChange(dataState)
            //delete Product success
            if(dataState != null){
                dataState.data?.let { data ->
                    data.response?.peekContent()?.let{ response ->
                        if(response.message.equals(SuccessHandling.DELETE_DONE)){
                            ProductMain()
                        }

                        if (response.message == CUSTOM_CATEGORY_UPDATE_DONE){
                            dialog.dismiss()
                            ProductMain()
                        }
                    }
                }
            }
            //set Product list get it from network
            dataState.data?.let { data ->
                data.data?.let{
                    it.getContentIfNotHandled()?.let{
                        it.productList.let {
                            viewModel.setProductList(it)
                        }
                    }

                }

            }
        })
        //submit list to recycler view
        viewModel.viewState.observe(viewLifecycleOwner, Observer { viewState ->
                productRecyclerAdapter.submitList(viewModel.getProductList())
        })


    }

    private fun addProduct() {
        add_product_button.setOnClickListener {
            findNavController().navigate(R.id.action_productFragment_to_createProductFragment)
        }
    }

    fun initProductRecyclerView(){
        product_recyclerview.apply {
            layoutManager = LinearLayoutManager(this@ProductFragment.context)
            val topSpacingDecorator = TopSpacingItemDecoration(0)
            removeItemDecoration(topSpacingDecorator) // does nothing if not applied already
            addItemDecoration(topSpacingDecorator)

            productRecyclerAdapter =
                ProductAdapter(
                    requestManager,
                    this@ProductFragment
                )
            addOnScrollListener(object: RecyclerView.OnScrollListener(){

                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                }
            })
            adapter = productRecyclerAdapter
        }

    }

    override fun onRefresh() {
        ProductMain()
        swipe_refresh.isRefreshing = false
    }

    override fun onItemSelected(item: Product,action:Int) {
        when (action){
            ActionConstants.SELECTED->{

                viewModel.setViewProductFields(item)
                findNavController().navigate(R.id.action_productFragment_to_viewProductFragment)
            }

            ActionConstants.DELETE->{
                val callback: AreYouSureCallback = object: AreYouSureCallback {
                    override fun proceed() {
                        deleteProduct(item.id)
                    }
                    override fun cancel() {
                        // ignore
                    }
                }
                uiCommunicationListener.onUIMessageReceived(
                    UIMessage(
                        getString(R.string.are_you_sure_delete),
                        UIMessageType.AreYouSureDialog(callback)
                    )
                )
            }
        }

    }

    var selectedProduct:Product? = null
    override fun onItemMoved(item: Product) {
        selectedProduct=item
        showFilterDialog()
    }

    lateinit var dialog:BottomSheetDialog
    private fun showFilterDialog() {
        dialog = BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme)
        dialogView = layoutInflater.inflate(R.layout.dialog_move_product_custom_category, null)
        dialog.setCancelable(true)
        dialog.setContentView(dialogView)

        val categoryRecyclerView = dialogView.findViewById<RecyclerView>(R.id.move_product_category)
        categoryProductAdapter = OrderFilterAdapter(this@ProductFragment)
        initCategoryRecyclerView(categoryRecyclerView, categoryProductAdapter!!)

        categoryProductAdapter!!.submitList(
            viewModel.getCustomCategoryFields().map { it.name },
            ""
        )

        dialog.show()
    }

    private fun initCategoryRecyclerView(recyclerView: RecyclerView,recyclerAdapter:OrderFilterAdapter) {
        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@ProductFragment.context,LinearLayoutManager.HORIZONTAL,false)

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
            recyclerAdapter.stateRestorationPolicy= RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
            adapter = recyclerAdapter
        }
    }

    fun deleteProduct(id:Long){
        viewModel.setStateEvent(CustomCategoryStateEvent.DeleteProductEvent(id))
    }

    override fun onResume() {
        super.onResume()
        viewModel.clearViewProductFields()
        viewModel.clearProductFields()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // clear references (can leak memory)
        //viewModel.clearProductList()
        viewModel.clearProductFields()
        product_recyclerview.adapter = null
        setToolBareText("")
    }

    override fun onItemSelected(item: String) {
        val category = viewModel.getCustomCategoryFields().find { it.name == item }
        viewModel.setStateEvent(
            CustomCategoryStateEvent.UpdateProductsCustomCategoryEvent(
                listOf(selectedProduct!!.id),
                category!!.pk.toLong()
            )
        )
    }

    override fun onItemDeSelected(item: String) {

    }
}