package com.smartcity.provider.fragments.main.account

import androidx.fragment.app.FragmentFactory
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.RequestManager
import com.smartcity.provider.di.main.MainScope
import com.smartcity.provider.ui.main.account.*
import com.smartcity.provider.ui.main.account.notification.NotificationFragment
import com.smartcity.provider.ui.main.account.policy.PolicyFormFragment
import com.smartcity.provider.ui.main.account.policy.PolicyFormOptionFragment
import com.smartcity.provider.ui.main.account.policy.PolicyFragment
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
                    requestManager
                )
            }

            PolicyFragment::class.java.name -> {
                PolicyFragment(
                    viewModelFactory,
                    requestManager
                )
            }

            PolicyFormFragment::class.java.name -> {
                PolicyFormFragment(
                    viewModelFactory,
                    requestManager
                )
            }

            PolicyFormOptionFragment::class.java.name -> {
                PolicyFormOptionFragment(
                    viewModelFactory,
                    requestManager
                )
            }

            else -> {
                AccountFragment(
                    viewModelFactory,
                    requestManager)
            }
        }


}