package com.smartcity.provider.fragments.main.account

import androidx.fragment.app.FragmentFactory
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.RequestManager
import com.smartcity.provider.di.main.MainScope
import com.smartcity.provider.ui.main.account.AccountFragment
import com.smartcity.provider.ui.main.account.NotificationFragment
import javax.inject.Inject

@MainScope
class AccountFragmentFactory
@Inject
constructor(
    private val viewModelFactory: ViewModelProvider.Factory,
    private val requestManager: RequestManager
) : FragmentFactory() {

    override fun instantiate(classLoader: ClassLoader, className: String) =

        when (className) {

            AccountFragment::class.java.name -> {
                AccountFragment(
                    viewModelFactory,
                    requestManager)
            }

            NotificationFragment::class.java.name -> {
                NotificationFragment(
                    viewModelFactory,
                    requestManager)
            }

            else -> {
                AccountFragment(
                    viewModelFactory,
                    requestManager)
            }
        }


}