package com.grebnev.vknewsclient.di.modules

import com.grebnev.vknewsclient.data.network.ApiFactory
import com.grebnev.vknewsclient.data.network.ApiService
import com.grebnev.vknewsclient.data.repository.NewsFeedRepositoryImpl
import com.grebnev.vknewsclient.di.scopes.ApplicationScope
import com.grebnev.vknewsclient.domain.repository.NewsFeedRepository
import dagger.Binds
import dagger.Module
import dagger.Provides

@Module
interface DataModule {

    @ApplicationScope
    @Binds
    fun bindNewsFeedRepository(impl: NewsFeedRepositoryImpl): NewsFeedRepository

    companion object {
        @ApplicationScope
        @Provides
        fun provideApiService(): ApiService {
            return ApiFactory.apiService
        }
    }
}