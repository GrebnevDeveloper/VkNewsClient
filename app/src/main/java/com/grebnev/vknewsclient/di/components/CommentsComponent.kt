package com.grebnev.vknewsclient.di.components

import com.grebnev.vknewsclient.di.modules.CommentsViewModelModule
import com.grebnev.vknewsclient.domain.entity.FeedPost
import com.grebnev.vknewsclient.presentation.ViewModelFactory
import dagger.BindsInstance
import dagger.Subcomponent

@Subcomponent(
    modules = [
        CommentsViewModelModule::class
    ]
)
interface CommentsComponent {

    fun getViewModuleFactory(): ViewModelFactory

    @Subcomponent.Factory
    interface Factory {
        fun create(@BindsInstance feedPost: FeedPost): CommentsComponent
    }
}