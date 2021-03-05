package com.smartcity.provider.ui.auth

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.smartcity.provider.R

import com.smartcity.provider.di.auth.AuthScope

import kotlinx.android.synthetic.main.fragment_choose_service.*
import javax.inject.Inject

@AuthScope
class ChooseServiceFragment
@Inject
constructor(
    private val viewModelFactory: ViewModelProvider.Factory
): BaseAuthFragment(R.layout.fragment_choose_service){



    val viewModel: AuthViewModel by viewModels{
        viewModelFactory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.cancelActiveJobs()
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        next_choose_service_button.setOnClickListener {
            navCreateStore()
        }
    }

    private fun navCreateStore() {
        findNavController().navigate(R.id.action_chooseServiceFragment_to_createStoreFragment)
    }

    override fun cancelActiveJobs() {
        viewModel.cancelActiveJobs()
    }


}