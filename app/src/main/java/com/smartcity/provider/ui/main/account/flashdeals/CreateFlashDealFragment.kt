package com.smartcity.provider.ui.main.account.flashdeals

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.RequestManager
import com.smartcity.provider.R
import com.smartcity.provider.models.FlashDeal
import com.smartcity.provider.ui.AreYouSureCallback
import com.smartcity.provider.ui.UIMessage
import com.smartcity.provider.ui.UIMessageType
import com.smartcity.provider.ui.main.account.BaseAccountFragment
import com.smartcity.provider.ui.main.account.state.ACCOUNT_VIEW_STATE_BUNDLE_KEY
import com.smartcity.provider.ui.main.account.state.AccountStateEvent
import com.smartcity.provider.ui.main.account.state.AccountViewState
import com.smartcity.provider.ui.main.account.viewmodel.AccountViewModel
import com.smartcity.provider.ui.main.account.viewmodel.getSelectedOffer
import com.smartcity.provider.ui.main.account.viewmodel.setFlashDealsList
import com.smartcity.provider.ui.main.account.viewmodel.setOffersList
import com.smartcity.provider.util.SuccessHandling
import kotlinx.android.synthetic.main.fragment_create_flash_deal.*
import javax.inject.Inject


class CreateFlashDealFragment
@Inject
constructor(
    private val viewModelFactory: ViewModelProvider.Factory,
    private val requestManager: RequestManager
): BaseAccountFragment(R.layout.fragment_create_flash_deal)
{
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


        subscribeObservers()

        create_flash_button.setOnClickListener {
            createFlashDeal()
        }
    }

    private fun getFlashDeals() {
        viewModel.setStateEvent(
            AccountStateEvent.GetFlashDealsEvent()
        )
    }

    private fun subscribeObservers() {
        viewModel.dataState.observe(viewLifecycleOwner, Observer { dataState ->
            stateChangeListener.onDataStateChange(dataState)
            if(dataState != null){
                dataState.data?.let { data ->
                    data.response?.peekContent()?.let{ response ->

                        if(!data.response.hasBeenHandled){
                            if (response.message== SuccessHandling.CREATION_DONE){
                                getFlashDeals()
                            }
                        }

                        if(response.message== SuccessHandling.DONE_Flashes){
                            data.data?.let{
                                it.getContentIfNotHandled()?.let{
                                    it.flashDealsFields.flashDealsList.let {
                                        viewModel.setFlashDealsList(it)
                                    }
                                }
                                findNavController().popBackStack()
                            }
                        }

                    }
                }
            }
        })
    }

    private fun createFlashDeal() {

        val callback: AreYouSureCallback = object: AreYouSureCallback {
            override fun proceed() {
                val flash=FlashDeal(
                    -1,
                    input_flash_title.text.toString(),
                    input_flash_content.text.toString(),
                    -1,
                    ""
                )

                viewModel.setStateEvent(
                    AccountStateEvent.CreateFlashDealEvent(
                        flashDeal = flash
                    )
                )
            }
            override fun cancel() {
                // ignore
            }
        }
        uiCommunicationListener.onUIMessageReceived(
            UIMessage(
                getString(R.string.are_you_sure_publish),
                UIMessageType.AreYouSureDialog(callback)
            )
        )
    }
}