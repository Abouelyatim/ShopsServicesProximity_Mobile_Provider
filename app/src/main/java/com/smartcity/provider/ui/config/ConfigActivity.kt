package com.smartcity.provider.ui.config

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.fragment.app.FragmentFactory
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.smartcity.provider.BaseApplication
import com.smartcity.provider.R
import com.smartcity.provider.fragments.config.ConfigNavHostFragment
import com.smartcity.provider.ui.BaseActivity
import com.smartcity.provider.ui.config.viewmodel.ConfigViewModel
import com.smartcity.provider.ui.config.viewmodel.getStoreCategories
import com.smartcity.provider.ui.config.viewmodel.setStoreCategories
import com.smartcity.provider.ui.main.MainActivity
import com.smartcity.provider.util.PreferenceKeys
import com.smartcity.provider.util.SuccessHandling
import kotlinx.android.synthetic.main.activity_config.*
import javax.inject.Inject
import javax.inject.Named

class ConfigActivity: BaseActivity()
{
    @Inject
    @Named("ConfigFragmentFactory")
    lateinit var fragmentFactory: FragmentFactory

    @Inject
    lateinit var providerFactory: ViewModelProvider.Factory

    @Inject
    @Named("GetSharedPreferences")
    lateinit var sharedPreferences: SharedPreferences

    val viewModel: ConfigViewModel by viewModels {
        providerFactory
    }

    override fun inject() {
        (application as BaseApplication).configComponent()
            .inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        inject()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_config)
        subscribeObservers()
        onRestoreInstanceState()
    }

    override fun displayProgressBar(bool: Boolean) {
        if(bool){
            progress_bar.visibility = View.VISIBLE
        }
        else{
            progress_bar.visibility = View.GONE
        }
    }

    override fun expandAppBar() {
        // ignore
    }

    override fun displayBottomNavigation(bool: Boolean) {
        // ignore
    }

    fun onRestoreInstanceState(){
        val host = supportFragmentManager.findFragmentById(R.id.config_fragments_container)
        host?.let {
            // do nothing
        } ?: createNavHost()
    }

    private fun createNavHost(){
        val navHost = ConfigNavHostFragment.create(
            R.navigation.config_nav_graph
        )
        supportFragmentManager.beginTransaction()
            .replace(
                R.id.config_fragments_container,
                navHost,
                getString(R.string.ConfigNavHost)
            )
            .setPrimaryNavigationFragment(navHost)
            .commit()
    }

    private fun subscribeObservers(){
        val interestCenter=sharedPreferences.getBoolean(PreferenceKeys.CREATE_STORE_FLAG, false)
        if(interestCenter){
            navMainActivity()
        }

        viewModel.dataState.observe(this, Observer{ dataState ->
            onDataStateChange(dataState)
            dataState.data?.let { data ->
                data.data?.let {
                    it.peekContent().storeFields.storeCategory?.let {
                        viewModel.setStoreCategories(it)
                    }
                }
                data.response?.let{event ->
                    event.peekContent().let{ response ->
                        response.message?.let{ message ->
                            if(message.equals(SuccessHandling.STORE_CATEGORIES_DONE)){//after save selected store categories navigate to main
                                navMainActivity()
                            }
                        }
                    }
                }
            }
        })

        viewModel.viewState.observe(this, Observer{ viewState ->
            viewModel.getStoreCategories()?.let {
                if(it.isNotEmpty()){
                    saveCreateStoreFlag(sharedPreferences)
                    navMainActivity()
                }else{
                    onFinishCheckPreviousAuthUser()
                }
            }
        })
    }

    private fun saveCreateStoreFlag(sharedPreferences: SharedPreferences) {
        val editor=sharedPreferences.edit()
        editor.putBoolean(PreferenceKeys.CREATE_STORE_FLAG,true)
        editor.apply()
    }

    private fun onFinishCheckPreviousAuthUser(){
        fragment_container.visibility = View.VISIBLE
    }

    fun navMainActivity(){
        Log.d(TAG, "navMainActivity: called.")
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
        (application as BaseApplication).releaseConfigComponent()
    }
}