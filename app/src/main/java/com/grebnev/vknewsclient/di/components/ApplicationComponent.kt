package com.grebnev.vknewsclient.di.components

import com.grebnev.vknewsclient.di.modules.DataModule
import com.grebnev.vknewsclient.di.modules.ViewModelModule
import com.grebnev.vknewsclient.di.scopes.ApplicationScope
import com.grebnev.vknewsclient.presentation.main.MainActivity
import dagger.Component

@ApplicationScope
@Component(
    modules = [
        DataModule::class,
        ViewModelModule::class
    ]
)
interface ApplicationComponent {

    fun inject(mainActivity: MainActivity)

    fun getCommentsComponentFactory(): CommentsComponent.Factory
}