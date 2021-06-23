package com.smartcity.provider.ui.main.account.information

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.smartcity.provider.R
import com.smartcity.provider.models.Category
import com.smartcity.provider.ui.main.account.BaseAccountFragment
import com.smartcity.provider.ui.main.account.information.adapters.CategoriesAdapter
import com.smartcity.provider.ui.main.account.state.ACCOUNT_VIEW_STATE_BUNDLE_KEY
import com.smartcity.provider.ui.main.account.state.AccountStateEvent
import com.smartcity.provider.ui.main.account.state.AccountViewState
import com.smartcity.provider.ui.main.account.viewmodel.*
import com.smartcity.provider.util.SuccessHandling
import com.smartcity.provider.util.TopSpacingItemDecoration
import kotlinx.android.synthetic.main.fragment_category.*
import javax.inject.Inject


class CategoryFragment
@Inject
constructor(
    private val viewModelFactory: ViewModelProvider.Factory,
    private val requestManager: RequestManager
): BaseAccountFragment(R.layout.fragment_category),
    CategoriesAdapter.Interaction {

    private lateinit var categoriesAdapter: CategoriesAdapter

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

        initRecyclerView()
        subscribeObservers()
        viewModel.setStateEvent(
            AccountStateEvent.AllCategoriesEvent()
        )
    }

    private fun subscribeObservers() {
        viewModel.dataState.observe(viewLifecycleOwner, Observer { dataState ->
            stateChangeListener.onDataStateChange(dataState)
            if(dataState != null){
                dataState.data?.let { data ->
                    data.response?.peekContent()?.let{ response ->

                          if(!data.response.hasBeenHandled){
                              if (response.message==SuccessHandling.DONE_ALL_CATEGORIES){
                                  data.data?.let{
                                      it.peekContent()?.let{
                                          it.storeInformationFields.categoryList?.let {
                                              viewModel.setCategoriesList(it)
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
                viewModel.getCategoriesList(),
                viewModel.getSelectedCategoriesList()
            )
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
                    this@CategoryFragment
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

    override fun onItemSelected(value: Category) {
        viewModel.setSelectedCategory(value)
        navCategoryValue()
    }

    private fun navCategoryValue(){
        findNavController().navigate(R.id.action_categoryFragment_to_categoryValueFragment)
    }
}