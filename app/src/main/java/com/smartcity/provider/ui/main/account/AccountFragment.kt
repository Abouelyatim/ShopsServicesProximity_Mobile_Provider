package com.smartcity.provider.ui.main.account

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.RequestManager
import com.smartcity.provider.R
import com.smartcity.provider.ui.main.account.state.ACCOUNT_VIEW_STATE_BUNDLE_KEY
import com.smartcity.provider.ui.main.account.state.AccountViewState
import com.smartcity.provider.ui.main.account.viewmodel.AccountViewModel
import kotlinx.android.synthetic.main.fragment_account.*
import javax.inject.Inject


class AccountFragment
@Inject
constructor(
    private val viewModelFactory: ViewModelProvider.Factory,
    private val requestManager: RequestManager
): BaseAccountFragment(R.layout.fragment_account){

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
        stateChangeListener.displayBottomNavigation(true)

        notification_settings.setOnClickListener {
            navNotification()
        }

        policy_settings.setOnClickListener {
            navPolicy()
        }

        information_settings.setOnClickListener {
            navInformation()
        }

        discounts_settings.setOnClickListener {
            navDiscounts()
        }
    }

    fun navNotification(){
        findNavController().navigate(R.id.action_accountFragment_to_notificationFragment)
    }

    fun navPolicy(){
        findNavController().navigate(R.id.action_accountFragment_to_policyFragment)
    }

    fun navInformation(){
        findNavController().navigate(R.id.action_accountFragment_to_informationFragment)
    }

    fun navDiscounts(){
        findNavController().navigate(R.id.action_accountFragment_to_discountFragment)
    }
}