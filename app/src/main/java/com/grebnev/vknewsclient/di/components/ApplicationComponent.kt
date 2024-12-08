package com.grebnev.vknewsclient.di.components

import com.grebnev.vknewsclient.di.modules.DataModule
import com.grebnev.vknewsclient.di.modules.ViewModelModule
import com.grebnev.vknewsclient.di.scopes.ApplicationScope
import com.grebnev.vknewsclient.presentation.ViewModelFactory
import dagger.Component

@ApplicationScope
@Component(
    modules = [
        DataModule::class,
        ViewModelModule::class
    ]
)
interface ApplicationComponent {

    fun getViewModelFactory(): ViewModelFactory

    fun getCommentsComponentFactory(): CommentsComponent.Factory
}