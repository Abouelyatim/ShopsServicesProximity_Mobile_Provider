package com.smartcity.provider.di.main

import android.content.SharedPreferences
import com.smartcity.provider.api.main.OpenApiMainService
import com.smartcity.provider.persistence.AccountPropertiesDao
import com.smartcity.provider.persistence.AppDatabase
import com.smartcity.provider.persistence.BlogPostDao
import com.smartcity.provider.repository.main.AccountRepository
import com.smartcity.provider.repository.main.StoreRepository
import com.smartcity.provider.repository.main.OrderRepository
import com.smartcity.provider.repository.main.CustomCategoryRepository


import com.smartcity.provider.session.SessionManager
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
@Module
object MainModule {

    @JvmStatic
    @MainScope
    @Provides
    fun provideOpenApiMainService(retrofitBuilder: Retrofit.Builder): OpenApiMainService {
        return retrofitBuilder
            .build()
            .create(OpenApiMainService::class.java)
    }

    @JvmStatic
    @MainScope
    @Provides
    fun provideStoreRepository(
        openApiMainService: OpenApiMainService,
        accountPropertiesDao: AccountPropertiesDao,
        sessionManager: SessionManager
    ): StoreRepository {
        return StoreRepository(openApiMainService, accountPropertiesDao, sessionManager)
    }

    @JvmStatic
    @MainScope
    @Provides
    fun provideBlogPostDao(db: AppDatabase): BlogPostDao {
        return db.getBlogPostDao()
    }

    @JvmStatic
    @MainScope
    @Provides
    fun provideBlogRepository(
        openApiMainService: OpenApiMainService,
        sessionManager: SessionManager
    ): OrderRepository {
        return OrderRepository(openApiMainService, sessionManager)
    }

    @JvmStatic
    @MainScope
    @Provides
    fun provideCreateBlogRepository(
        openApiMainService: OpenApiMainService,
        blogPostDao: BlogPostDao,
        sessionManager: SessionManager
    ): CustomCategoryRepository {
        return CustomCategoryRepository(openApiMainService, blogPostDao, sessionManager)
    }

    @JvmStatic
    @MainScope
    @Provides
    fun provideAccountRepository(
        openApiMainService: OpenApiMainService,
        sessionManager: SessionManager
    ): AccountRepository {
        return AccountRepository(openApiMainService, sessionManager)
    }
}

















