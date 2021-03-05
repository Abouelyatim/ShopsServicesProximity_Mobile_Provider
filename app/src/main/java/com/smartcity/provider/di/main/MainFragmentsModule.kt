package com.smartcity.provider.di.main

import androidx.fragment.app.FragmentFactory
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.RequestManager
import com.smartcity.provider.fragments.main.store.StoreFragmentFactory
import com.smartcity.provider.fragments.main.blog.BlogFragmentFactory
import com.smartcity.provider.fragments.main.custom_category.CustomCategoryFragmentFactory


import dagger.Module
import dagger.Provides
import javax.inject.Named
@Module
object MainFragmentsModule {

    @JvmStatic
    @MainScope
    @Provides
    @Named("AccountFragmentFactory")
    fun provideAccountFragmentFactory(
        viewModelFactory: ViewModelProvider.Factory,
        requestManager: RequestManager
    ): FragmentFactory {
        return StoreFragmentFactory(
            viewModelFactory,
            requestManager
        )
    }

    @JvmStatic
    @MainScope
    @Provides
    @Named("BlogFragmentFactory")
    fun provideBlogFragmentFactory(
        viewModelFactory: ViewModelProvider.Factory,
        requestManager: RequestManager
    ): FragmentFactory {
        return BlogFragmentFactory(
            viewModelFactory,
            requestManager
        )
    }

    @JvmStatic
    @MainScope
    @Provides
    @Named("CreateBlogFragmentFactory")
    fun provideCreateBlogFragmentFactory(
        viewModelFactory: ViewModelProvider.Factory,
        requestManager: RequestManager
    ): FragmentFactory {
        return CustomCategoryFragmentFactory(
            viewModelFactory,
            requestManager
        )
    }
}