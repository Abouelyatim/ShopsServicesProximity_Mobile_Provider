package com.smartcity.provider.ui.config

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.smartcity.provider.R
import com.smartcity.provider.models.Category
import com.smartcity.provider.ui.config.state.ConfigStateEvent
import com.smartcity.provider.ui.config.viewmodel.*
import com.smartcity.provider.ui.main.account.information.adapters.CategoriesAdapter
import com.smartcity.provider.util.SuccessHandling
import com.smartcity.provider.util.TopSpacingItemDecoration
import kotlinx.android.synthetic.main.fragment_category_config.*
import javax.inject.Inject


class CategoryConfigFragment
@Inject
constructor(
    private val viewModelFactory: ViewModelProvider.Factory
): BaseConfigFragment(R.layout.fragment_category_config),
    CategoriesAdapter.Interaction{

    private lateinit var categoriesAdapter: CategoriesAdapter

    val viewModel: ConfigViewModel by viewModels{
        viewModelFactory
    }

    override fun cancelActiveJobs() {
        viewModel.cancelActiveJobs()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.cancelActiveJobs()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRecyclerView()
        subscribeObservers()
        getAllCategories()
        saveCategories()
    }

    private fun saveCategories() {
        save_categories.setOnClickListener {
            viewModel.setStateEvent(
                ConfigStateEvent.SetStoreCategoriesEvent(
                    categories = viewModel.getSelectedStoreCategories().flatMap { it.subCategorys }
                )
            )
        }
    }

    private fun subscribeObservers() {
        viewModel.dataState.observe(viewLifecycleOwner, Observer { dataState ->
            stateChangeListener.onDataStateChange(dataState)
            if(dataState != null){
                dataState.data?.let { data ->
                    data.response?.peekContent()?.let{ response ->

                        if(!data.response.hasBeenHandled){
                            if (response.message== SuccessHandling.DONE_ALL_CATEGORIES){
                                data.data?.let{
                                    it.peekContent()?.let{
                                        it.storeFields.allCategoryStore?.let {
                                            viewModel.setListAllCategoryStore(it)
                                        }

                                    }
                                }

                            }
                        }
                    }
                }
            }
        })

        //submit list to recycler view
        viewModel.viewState.observe(viewLifecycleOwner, Observer { viewState ->
            categoriesAdapter.submitList(
                viewModel.getAllCategoryStore(),
                viewModel.getSelectedStoreCategories()
            )

            if(viewModel.getSelectedStoreCategories().isEmpty()){
                save_categories.visibility = View.GONE
            }else{
                save_categories.visibility = View.VISIBLE
            }
        })
    }

    private fun initRecyclerView() {
        category_recyclerview.apply {
            layoutManager = FlexboxLayoutManager(context)
            (layoutManager as FlexboxLayoutManager).justifyContent = JustifyContent.CENTER
            (layoutManager as FlexboxLayoutManager).flexWrap= FlexWrap.WRAP

            val topSpacingDecorator = TopSpacingItemDecoration(0)
            removeItemDecoration(topSpacingDecorator) // does nothing if not applied already
            addItemDecoration(topSpacingDecorator)

            categoriesAdapter =
                CategoriesAdapter(
                    this@CategoryConfigFragment
                )
            addOnScrollListener(object: RecyclerView.OnScrollListener(){

                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                }
            })
            adapter = categoriesAdapter
        }
    }

    private fun getAllCategories() {
        viewModel.setStateEvent(
            ConfigStateEvent.GetAllCategoriesEvent()
        )
    }

    override fun onItemSelected(value: Category) {
        viewModel.setSelectedCategory(value)
        navCategoryValue()
    }

    private fun navCategoryValue() {
       findNavController().navigate(R.id.action_categoryConfigFragment_to_categoryValueConfigFragment)
    }
}