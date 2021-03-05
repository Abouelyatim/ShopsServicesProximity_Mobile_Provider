package com.smartcity.provider.ui.main.store

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.smartcity.provider.R
import com.smartcity.provider.models.CustomCategory
import com.smartcity.provider.models.product.Product
import com.smartcity.provider.ui.main.store.state.ACCOUNT_VIEW_STATE_BUNDLE_KEY
import com.smartcity.provider.ui.main.store.state.StoreStateEvent
import com.smartcity.provider.ui.main.store.state.StoreViewState
import com.smartcity.provider.util.RightSpacingItemDecoration
import com.smartcity.provider.util.SuccessHandling
import com.smartcity.provider.util.TopSpacingItemDecoration
import kotlinx.android.synthetic.main.fragment_store.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

class StoreFragment
@Inject
constructor(
    private val viewModelFactory: ViewModelProvider.Factory,
    private val requestManager: RequestManager
): BaseStoreFragment(R.layout.fragment_store),
    ViewCustomCategoryAdapter.Interaction,
    ViewCustomCategoryAdapter.InteractionAll,
    ViewProductAdapter.Interaction{

    private lateinit var customCategoryrecyclerAdapter: ViewCustomCategoryAdapter
    private lateinit var productRecyclerAdapter: ViewProductAdapter

    val viewModel: StoreViewModel by viewModels{
        viewModelFactory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cancelActiveJobs()
        // Restore state after process death
        savedInstanceState?.let { inState ->
            (inState[ACCOUNT_VIEW_STATE_BUNDLE_KEY] as StoreViewState?)?.let { viewState ->
                viewModel.setViewState(viewState)
            }
        }
    }

    override fun cancelActiveJobs(){
        viewModel.cancelActiveJobs()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        initCustomCategoryRecyclerView()
        initProductRecyclerView()
        CustomCategoryMain()
        subscribeObservers()
    }

    private fun initProductRecyclerView() {
        view_product_recyclerview.apply {
            layoutManager = LinearLayoutManager(this@StoreFragment.context)
            val topSpacingDecorator = TopSpacingItemDecoration(0)
            removeItemDecoration(topSpacingDecorator) // does nothing if not applied already
            addItemDecoration(topSpacingDecorator)

            productRecyclerAdapter =
                ViewProductAdapter(
                    requestManager,
                    this@StoreFragment
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

    fun CustomCategoryMain(){
        viewModel.setStateEvent(StoreStateEvent.CustomCategoryMain())
    }

    fun initCustomCategoryRecyclerView(){
        view_custom_category_recyclerview.apply {
            layoutManager = LinearLayoutManager(this@StoreFragment.context,LinearLayoutManager.HORIZONTAL, false)

            val rightSpacingDecorator = RightSpacingItemDecoration(60)
            removeItemDecoration(rightSpacingDecorator) // does nothing if not applied already
            addItemDecoration(rightSpacingDecorator)

            customCategoryrecyclerAdapter =
                ViewCustomCategoryAdapter(
                    this@StoreFragment,
                    this@StoreFragment
                )
            addOnScrollListener(object: RecyclerView.OnScrollListener(){

                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                }
            })
            adapter = customCategoryrecyclerAdapter
        }

    }

    private fun subscribeObservers(){
        viewModel.dataState.observe(viewLifecycleOwner, Observer { dataState ->
            stateChangeListener.onDataStateChange(dataState)

            if(dataState != null){
                //set Product list get it from network
                dataState.data?.let { data ->
                    data.response?.peekContent()?.let{ response ->
                        when(response.message){

                            SuccessHandling.DONE_Product_Main->{
                                data.data?.let{
                                    it.peekContent()?.let{
                                        it.viewProductList.let {
                                            viewModel.setViewProductList(it)
                                        }
                                    }

                                }
                            }

                            SuccessHandling.DONE_Custom_Category_Main->{
                                AllProduct()
                                data.data?.let{
                                    it.peekContent()?.let{
                                        viewModel.setViewCustomCategoryFields(it.viewCustomCategoryFields)
                                    }

                                }
                            }

                            SuccessHandling.DONE_All_Product->{
                                data.data?.let{
                                    it.peekContent()?.let{
                                        it.viewProductList.let {
                                            viewModel.setViewProductList(it)
                                        }
                                    }

                                }
                            }


                            else ->{

                            }

                        }
                    }
                }
            }
        })
        //submit list to recycler view
        viewModel.viewState.observe(viewLifecycleOwner, Observer { viewState ->
            customCategoryrecyclerAdapter.submitList(viewModel.getViewCustomCategoryFields())
            productRecyclerAdapter.submitList(viewModel.getViewProductList().products)
        })
    }
    fun ProductMain(id:Long){
        viewModel.setStateEvent(
            StoreStateEvent.ProductMain(
                id
            ))
    }

    fun AllProduct(){
        viewModel.setStateEvent(
            StoreStateEvent.AllProduct()
        )
    }

    override fun onItemSelected(position: Int, item: CustomCategory) {
        view_custom_category_recyclerview.adapter!!.notifyDataSetChanged()
        ProductMain(item.pk.toLong())
    }

    override fun onItemSelected(position: Int, item: Product, action: Int) {
        TODO("Not yet implemented")

    }

    override fun onItemAddSelected() {
        view_custom_category_recyclerview.adapter!!.notifyDataSetChanged()
        AllProduct()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // clear references (can leak memory)
        customCategoryrecyclerAdapter.resetSelectedPosition()
        view_custom_category_recyclerview.adapter = null
        view_product_recyclerview.adapter=null
    }




}