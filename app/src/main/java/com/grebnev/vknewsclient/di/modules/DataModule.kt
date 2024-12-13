package com.grebnev.vknewsclient.di.modules

import com.grebnev.vknewsclient.data.network.ApiFactory
import com.grebnev.vknewsclient.data.network.ApiService
import com.grebnev.vknewsclient.data.repository.NewsFeedRepositoryImpl
import com.grebnev.vknewsclient.data.repository.ProfileInfoRepositoryImpl
import com.grebnev.vknewsclient.di.scopes.ApplicationScope
import com.grebnev.vknewsclient.domain.repository.NewsFeedRepository
import com.grebnev.vknewsclient.domain.repository.ProfileInfoRepository
import dagger.Binds
import dagger.Module
import dagger.Provides

@Module
interface DataModule {

    @ApplicationScope
    @Binds
    fun bindNewsFeedRepository(impl: NewsFeedRepositoryImpl): NewsFeedRepository

    @ApplicationScope
    @Binds
    fun bindProfileInfoRepository(impl: ProfileInfoRepositoryImpl): ProfileInfoRepository

    companion object {
        @ApplicationScope
        @Provides
        fun provideApiService(): ApiService {
            return ApiFactory.apiService
        }
    }
}