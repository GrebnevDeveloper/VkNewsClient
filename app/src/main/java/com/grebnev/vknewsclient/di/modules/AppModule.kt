package com.grebnev.vknewsclient.di.modules

import android.app.Application
import android.content.Context
import com.grebnev.vknewsclient.di.scopes.ApplicationScope
import com.grebnev.vknewsclient.presentation.ErrorMessageProvider
import dagger.Module
import dagger.Provides


@Module
class AppModule(private val application: Application) {

    @ApplicationScope
    @Provides
    fun provideApplication(): Application = application

    @ApplicationScope
    @Provides
    fun provideApplicationContext(): Context = application.applicationContext

    @ApplicationScope
    @Provides
    fun provideErrorMessage(context: Context): ErrorMessageProvider = ErrorMessageProvider(context)
}
