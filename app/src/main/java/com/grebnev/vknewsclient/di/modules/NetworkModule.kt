package com.grebnev.vknewsclient.di.modules

import com.grebnev.vknewsclient.data.network.ApiFactory
import com.grebnev.vknewsclient.data.network.ApiService
import com.grebnev.vknewsclient.data.source.AccessTokenSource
import com.grebnev.vknewsclient.di.scopes.ApplicationScope
import dagger.Module
import dagger.Provides

@Module
class NetworkModule {

    @ApplicationScope
    @Provides
    fun provideApiService(): ApiService {
        return ApiFactory.apiService
    }

    @ApplicationScope
    @Provides
    fun provideAccessToken(): AccessTokenSource {
        return AccessTokenSource()
    }
}