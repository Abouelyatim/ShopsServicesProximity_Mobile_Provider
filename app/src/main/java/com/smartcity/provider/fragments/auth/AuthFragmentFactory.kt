package com.smartcity.provider.fragments.auth

import androidx.fragment.app.FragmentFactory
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.RequestManager
import com.smartcity.provider.di.auth.AuthScope
import com.smartcity.provider.ui.auth.*
import javax.inject.Inject

@AuthScope
class AuthFragmentFactory
@Inject
constructor(
    private val viewModelFactory: ViewModelProvider.Factory,
    private val requestManager: RequestManager
) : FragmentFactory() {

    override fun instantiate(classLoader: ClassLoader, className: String) =

        when (className) {

            LauncherFragment::class.java.name -> {
                LauncherFragment(viewModelFactory)
            }

            LoginFragment::class.java.name -> {
                LoginFragment(viewModelFactory)
            }

            RegisterFragment::class.java.name -> {
                RegisterFragment(viewModelFactory)
            }

            ForgotPasswordFragment::class.java.name -> {
                ForgotPasswordFragment(viewModelFactory)
            }

            ChooseServiceFragment::class.java.name -> {
                ChooseServiceFragment(viewModelFactory)
            }

            CreateStoreFragment::class.java.name -> {
                CreateStoreFragment(viewModelFactory,requestManager)
            }
            else -> {
                LauncherFragment(viewModelFactory)
            }
        }


}