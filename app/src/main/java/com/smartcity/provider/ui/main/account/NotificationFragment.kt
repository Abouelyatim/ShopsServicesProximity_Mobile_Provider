package com.smartcity.provider.ui.main.account

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.RequestManager
import com.google.android.material.switchmaterial.SwitchMaterial
import com.smartcity.provider.R
import com.smartcity.provider.ui.main.account.state.ACCOUNT_VIEW_STATE_BUNDLE_KEY
import com.smartcity.provider.ui.main.account.state.AccountStateEvent
import com.smartcity.provider.ui.main.account.state.AccountViewState
import com.smartcity.provider.ui.main.account.viewmodel.AccountViewModel
import com.smartcity.provider.util.NotificationSettings.Companion.ORDERS_NOTIFICATION
import com.smartcity.provider.util.NotificationSettings.Companion.SOUND_NOTIFICATION
import com.smartcity.provider.util.NotificationSettings.Companion.VIBRATION_NOTIFICATION
import com.smartcity.provider.util.SuccessHandling
import kotlinx.android.synthetic.main.fragment_notification.*
import javax.inject.Inject


class NotificationFragment
@Inject
constructor(
    private val viewModelFactory: ViewModelProvider.Factory,
    private val requestManager: RequestManager
): BaseAccountFragment(R.layout.fragment_notification){

    val viewModel: AccountViewModel by viewModels{
        viewModelFactory
    }

    private lateinit var  switches:List<Pair<String,SwitchMaterial>>

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
        saveNotificationSettings()
        initSwitches()
        getSettings()
    }

    private fun getSettings() {
        viewModel.setStateEvent(
            AccountStateEvent.GetNotificationSettings()
        )
    }

    private fun saveNotificationSettings() {
        save_settings_button.setOnClickListener {
            viewModel.setStateEvent(
                AccountStateEvent.SaveNotificationSettings(
                    getCheckedSwitch()
                )
            )
        }
    }

    private fun initSwitches() {
        switches= listOf(
            Pair(ORDERS_NOTIFICATION,switch_orders),
            Pair(VIBRATION_NOTIFICATION,switch_vibration),
            Pair(SOUND_NOTIFICATION,switch_sound)
        )
    }

    private fun setSwitchesUi(clickedSwitch:List<String>){
        switches.map {
            if(it.first in clickedSwitch){
                it.second.isChecked=true
            }
        }
    }

    private fun getCheckedSwitch():List<String>{
        val settings= mutableListOf<String>()
        switches.map {
            if(it.second.isChecked){
                settings.add(it.first)
            }
        }
        return settings
    }

    private fun subscribeObservers() {
        viewModel.dataState.observe(viewLifecycleOwner, Observer { dataState ->
            stateChangeListener.onDataStateChange(dataState)
            if(dataState != null){
                dataState.data?.let { data ->
                    data.response?.peekContent()?.let{ response ->

                        if(!data.response.hasBeenHandled){
                            if (response.message==SuccessHandling.RESPONSE_SAVE_NOTIFICATION_SETTINGS_DONE){
                                navAccount()
                            }
                        }

                        if (response.message==SuccessHandling.RESPONSE_GET_NOTIFICATION_SETTINGS_DONE){
                            data.data?.let{
                                it.peekContent()?.let{
                                    setSwitchesUi(it.notificationSettings)
                                }
                            }
                        }
                    }
                }
            }
        })
    }

    private fun navAccount(){
        findNavController().popBackStack()
    }

    override fun onDestroy() {
        super.onDestroy()
        stateChangeListener.displayBottomNavigation(true)
    }
}