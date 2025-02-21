package com.grebnev.vknewsclient.di.modules

import androidx.lifecycle.ViewModel
import com.grebnev.vknewsclient.di.keys.ViewModelKey
import com.grebnev.vknewsclient.presentation.main.MainViewModel
import com.grebnev.vknewsclient.presentation.news.recommendations.RecommendationsFeedViewModel
import com.grebnev.vknewsclient.presentation.news.subscriptions.SubscriptionsFeedViewModel
import com.grebnev.vknewsclient.presentation.profile.ProfileInfoViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
interface ViewModelModule {

    @IntoMap
    @ViewModelKey(RecommendationsFeedViewModel::class)
    @Binds
    fun bindNewsFeedViewModel(viewModel: RecommendationsFeedViewModel): ViewModel

    @IntoMap
    @ViewModelKey(SubscriptionsFeedViewModel::class)
    @Binds
    fun bindSubscriptionsFeedViewModel(viewModel: SubscriptionsFeedViewModel): ViewModel

    @IntoMap
    @ViewModelKey(MainViewModel::class)
    @Binds
    fun bindMainViewModel(viewModel: MainViewModel): ViewModel

    @IntoMap
    @ViewModelKey(ProfileInfoViewModel::class)
    @Binds
    fun bindProfileInfoViewModel(viewModel: ProfileInfoViewModel): ViewModel
}