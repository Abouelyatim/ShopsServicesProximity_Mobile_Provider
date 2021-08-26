package com.smartcity.provider.ui.config

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.smartcity.provider.R
import com.smartcity.provider.ui.config.state.ConfigStateEvent
import com.smartcity.provider.ui.config.viewmodel.ConfigViewModel
import kotlinx.android.synthetic.main.fragment_choose_service_config.*
import javax.inject.Inject

class ChooseServiceConfigFragment
@Inject
constructor(
    private val viewModelFactory: ViewModelProvider.Factory
): BaseConfigFragment(R.layout.fragment_choose_service_config){

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

        getStoreCategories()
        subscribeObservers()
        next_choose_service_button.setOnClickListener {
            findNavController().navigate(R.id.action_chooseServiceConfigFragment_to_createStoreConfigFragment)
        }
    }

    private fun getStoreCategories() {
        viewModel.setStateEvent(
            ConfigStateEvent.GetStoreCategoriesEvent()
        )
    }

    private fun subscribeObservers() {
        viewModel.dataState.observe(viewLifecycleOwner, Observer{ dataState ->
            stateChangeListener.onDataStateChange(dataState)

        })
    }
}