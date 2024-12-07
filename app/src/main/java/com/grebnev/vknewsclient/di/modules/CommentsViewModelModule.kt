package com.grebnev.vknewsclient.di.modules

import androidx.lifecycle.ViewModel
import com.grebnev.vknewsclient.di.keys.ViewModelKey
import com.grebnev.vknewsclient.presentation.comments.CommentsViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
interface CommentsViewModelModule {

    @IntoMap
    @ViewModelKey(CommentsViewModel::class)
    @Binds
    fun bindCommentsViewModel(viewModel: CommentsViewModel): ViewModel
}