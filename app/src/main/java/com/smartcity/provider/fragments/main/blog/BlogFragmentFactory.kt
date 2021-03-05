package com.smartcity.provider.fragments.main.blog

import androidx.fragment.app.FragmentFactory
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.RequestManager
import com.smartcity.provider.di.main.MainScope
import com.smartcity.provider.ui.main.blog.BlogFragment
import com.smartcity.provider.ui.main.blog.UpdateBlogFragment
import com.smartcity.provider.ui.main.blog.ViewBlogFragment
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

            BlogFragment::class.java.name -> {
                BlogFragment(viewModelFactory, requestManager)
            }

            ViewBlogFragment::class.java.name -> {
                ViewBlogFragment(viewModelFactory, requestManager)
            }

            UpdateBlogFragment::class.java.name -> {
                UpdateBlogFragment(viewModelFactory, requestManager)
            }

            else -> {
                BlogFragment(viewModelFactory, requestManager)
            }
        }


}