package com.grebnev.vknewsclient.di.modules

import androidx.lifecycle.ViewModel
import com.grebnev.vknewsclient.di.keys.ViewModelKey
import com.grebnev.vknewsclient.presentation.comments.CommentsViewModel
import com.grebnev.vknewsclient.presentation.main.MainViewModel
import com.grebnev.vknewsclient.presentation.news.NewsFeedViewModel
import com.grebnev.vknewsclient.presentation.profile.ProfileInfoViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
interface ViewModelModule {

    @IntoMap
    @ViewModelKey(NewsFeedViewModel::class)
    @Binds
    fun bindNewsFeedViewModel(viewModel: NewsFeedViewModel): ViewModel

    @IntoMap
    @ViewModelKey(MainViewModel::class)
    @Binds
    fun bindMainViewModel(viewModel: MainViewModel): ViewModel

    @IntoMap
    @ViewModelKey(ProfileInfoViewModel::class)
    @Binds
    fun bindProfileInfoViewModel(viewModel: ProfileInfoViewModel): ViewModel
}