package com.smartcity.provider.ui.main.account.policy

import android.os.Bundle
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.View
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.RequestManager
import com.smartcity.provider.R
import com.smartcity.provider.models.SelfPickUpOptions
import com.smartcity.provider.ui.*
import com.smartcity.provider.ui.main.account.BaseAccountFragment
import com.smartcity.provider.ui.main.account.state.ACCOUNT_VIEW_STATE_BUNDLE_KEY
import com.smartcity.provider.ui.main.account.state.AccountViewState
import com.smartcity.provider.ui.main.account.viewmodel.*
import com.smartcity.provider.util.ErrorHandling.Companion.ERROR_FILL_ALL_INFORMATION
import kotlinx.android.synthetic.main.fragment_policy_form.*
import javax.inject.Inject

class PolicyFormFragment
@Inject
constructor(
    private val viewModelFactory: ViewModelProvider.Factory,
    private val requestManager: RequestManager
): BaseAccountFragment(R.layout.fragment_policy_form){

    val viewModel: AccountViewModel by viewModels{
        viewModelFactory
    }

    private lateinit var  radios:List<Pair<RadioButton, SelfPickUpOptions>>

    private lateinit var extendable:List<Pair<ImageView, TextView>>

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

        initRadisButtons()
        initExtendable()
        setDeliveryConfig()

        option_policy_button.setOnClickListener {
            if(viewModel.getPolicyConfigurationDelivery()==null
                || viewModel.getPolicyConfigurationSelfPickUpOption()==null){
                showErrorDialog(ERROR_FILL_ALL_INFORMATION)
            }else{
                navPolicyOption()
            }
        }
    }

    fun navPolicyOption(){
        findNavController().navigate(R.id.action_policyFormFragment_to_policyFormOptionFragment)
    }

    fun setDeliveryConfig(){
        delivery_group.setOnCheckedChangeListener { radioGroup, i ->
            val delivery= when (radioGroup.checkedRadioButtonId) {
                R.id.delivery_yes -> true
                R.id.delivery_no -> false
                else -> false
            }
            viewModel.setPolicyConfigurationDelivery(delivery)
        }
    }

    fun initExtendable(){
        val list= mutableListOf<Pair<ImageView, TextView>>()
        list.add(Pair(expand_without_payment,description_without_payment))
        list.add(Pair(expand_extend_range,description_extend_range))
        list.add(Pair(expand_total_payment,description_total_payment))
        list.add(Pair(expand_part_fixed,description_part_fixed))
        list.add(Pair(expand_part_range,description_part_range))
        extendable=list
        extendableBehavior()
    }

    fun extendableBehavior(){
        extendable.map {expand->
            expand.first.setOnClickListener {
                if (expand.second.visibility==View.GONE){
                    TransitionManager.beginDelayedTransition(container, AutoTransition())
                    expand.second.visibility=View.VISIBLE
                    expand.first.setBackgroundResource(R.drawable.ic_baseline_close_24)
                }else{
                    TransitionManager.beginDelayedTransition(container, AutoTransition())
                    expand.second.visibility=View.GONE
                    expand.first.setBackgroundResource(R.drawable.ic_baseline_add_24)
                }
            }
        }
    }

    fun initRadisButtons(){
        val list= mutableListOf<Pair<RadioButton, SelfPickUpOptions>>()
        list.add(Pair( radio_without_payment, SelfPickUpOptions.SELF_PICK_UP))
        list.add(Pair( radio_extend_fixed, SelfPickUpOptions.SELF_PICK_UP_EXTEND_PERCENTAGE))
        list.add(Pair( radio_total_payment, SelfPickUpOptions.SELF_PICK_UP_TOTAL))
        list.add(Pair( radio_part_fixed, SelfPickUpOptions.SELF_PICK_UP_PART_PERCENTAGE))
        list.add(Pair(radio_part_range, SelfPickUpOptions.SELF_PICK_UP_PART_RANGE))
        radios=list
        radioButtonsBehavior()
    }

    fun radioButtonsBehavior(){
        radios.map {radio->
            radio.first.setOnClickListener {
                radio.first.isChecked=true
                viewModel.setPolicyConfigurationSelfPickUpOption(radio.second)
                radios.filter { it != radio }.map {
                    it.first.isChecked=false
                }
            }
        }
    }

    fun showErrorDialog(errorMessage: String){
        stateChangeListener.onDataStateChange(
            DataState(
                Event(StateError(Response(errorMessage, ResponseType.Dialog()))),
                Loading(isLoading = false),
                Data(Event.dataEvent(null), null)
            )
        )
    }
}