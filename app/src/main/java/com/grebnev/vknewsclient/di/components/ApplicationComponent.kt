package com.grebnev.vknewsclient.di.components

import com.grebnev.vknewsclient.di.modules.AppModule
import com.grebnev.vknewsclient.di.modules.NetworkModule
import com.grebnev.vknewsclient.di.modules.RepositoryModule
import com.grebnev.vknewsclient.di.modules.ViewModelModule
import com.grebnev.vknewsclient.di.scopes.ApplicationScope
import com.grebnev.vknewsclient.presentation.ViewModelFactory
import dagger.Component

@ApplicationScope
@Component(
    modules = [
        AppModule::class,
        NetworkModule::class,
        RepositoryModule::class,
        ViewModelModule::class
    ]
)
interface ApplicationComponent {

    fun getViewModelFactory(): ViewModelFactory

    fun getCommentsComponentFactory(): CommentsComponent.Factory
}