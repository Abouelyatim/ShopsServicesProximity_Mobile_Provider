package com.smartcity.provider.fragments.main.blog

import android.content.SharedPreferences
import androidx.fragment.app.FragmentFactory
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.RequestManager
import com.smartcity.provider.di.main.MainScope
import com.smartcity.provider.ui.main.order.OrderFragment

import javax.inject.Inject

@MainScope
class BlogFragmentFactory
@Inject
constructor(
    private val viewModelFactory: ViewModelProvider.Factory,
    private val requestManager: RequestManager,
    private val sharedPreferences: SharedPreferences
) : FragmentFactory() {

    override fun instantiate(classLoader: ClassLoader, className: String) =

        when (className) {

            OrderFragment::class.java.name -> {
                OrderFragment(viewModelFactory, requestManager,sharedPreferences)
            }



            else -> {
                OrderFragment(viewModelFactory, requestManager,sharedPreferences)
            }
        }


}