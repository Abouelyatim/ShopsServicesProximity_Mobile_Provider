package com.smartcity.provider.di.main

import com.smartcity.provider.api.main.OpenApiMainService
import com.smartcity.provider.persistence.AccountPropertiesDao
import com.smartcity.provider.persistence.AppDatabase
import com.smartcity.provider.persistence.BlogPostDao
import com.smartcity.provider.repository.main.StoreRepository
import com.smartcity.provider.repository.main.BlogRepository
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
    fun provideAccountRepository(
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
        blogPostDao: BlogPostDao,
        sessionManager: SessionManager
    ): BlogRepository {
        return BlogRepository(openApiMainService, blogPostDao, sessionManager)
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
}

















