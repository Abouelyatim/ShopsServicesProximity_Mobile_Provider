package com.smartcity.provider.fragments.main.blog

import androidx.fragment.app.FragmentFactory
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.RequestManager
import com.smartcity.provider.di.main.MainScope
import com.smartcity.provider.ui.main.order.OrderFragment
import com.smartcity.provider.ui.main.order.ViewOrderFragment
import com.smartcity.provider.ui.main.order.search.ReceiverNameFragment
import com.smartcity.provider.ui.main.order.search.ScanQrCodeFragment
import com.smartcity.provider.ui.main.order.search.SearchOrdersFragment
import com.smartcity.provider.ui.main.order.search.ViewSearchOrdersFragment
import javax.inject.Inject

@MainScope
class BlogFragmentFactory
@Inject
constructor(
    private val viewModelFactory: ViewModelProvider.Factory,
    private val requestManager: RequestManager
) : FragmentFactory() {

    override fun instantiate(classLoader: ClassLoader, className: String) =

        when (className) {

            OrderFragment::class.java.name -> {
                OrderFragment(viewModelFactory, requestManager)
            }

            ViewOrderFragment::class.java.name -> {
                ViewOrderFragment(viewModelFactory, requestManager)
            }

            SearchOrdersFragment::class.java.name -> {
                SearchOrdersFragment(viewModelFactory, requestManager)
            }

            ScanQrCodeFragment::class.java.name -> {
                ScanQrCodeFragment(viewModelFactory, requestManager)
            }

            ReceiverNameFragment::class.java.name -> {
                ReceiverNameFragment(viewModelFactory, requestManager)
            }

            ViewSearchOrdersFragment::class.java.name -> {
                ViewSearchOrdersFragment(viewModelFactory, requestManager)
            }
            else -> {
                OrderFragment(viewModelFactory, requestManager)
            }
        }


}