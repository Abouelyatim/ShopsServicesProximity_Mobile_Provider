package com.smartcity.provider.di.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.smartcity.provider.di.auth.keys.MainViewModelKey
import com.smartcity.provider.ui.main.store.StoreViewModel
import com.smartcity.provider.ui.main.blog.viewmodel.BlogViewModel
import com.smartcity.provider.ui.main.custom_category.CustomCategoryViewModel

import com.smartcity.provider.viewmodels.MainViewModelFactory
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class MainViewModelModule {

    @Binds
    abstract fun bindViewModelFactory(factory: MainViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @MainViewModelKey(StoreViewModel::class)
    abstract fun bindAccountViewModel(accoutViewModel: StoreViewModel): ViewModel

    @Binds
    @IntoMap
    @MainViewModelKey(BlogViewModel::class)
    abstract fun bindBlogViewModel(blogViewModel: BlogViewModel): ViewModel

    @Binds
    @IntoMap
    @MainViewModelKey(CustomCategoryViewModel::class)
    abstract fun bindCreateBlogViewModel(customCategoryViewModel: CustomCategoryViewModel): ViewModel
}








