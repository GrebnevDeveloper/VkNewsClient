package com.grebnev.vknewsclient.presentation

import android.app.Application
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.grebnev.vknewsclient.BuildConfig
import com.grebnev.vknewsclient.di.components.ApplicationComponent
import com.grebnev.vknewsclient.di.components.DaggerApplicationComponent
import com.grebnev.vknewsclient.di.modules.AppModule
import com.vk.id.VKID
import timber.log.Timber


class App : Application() {

    val component: ApplicationComponent by lazy {
        DaggerApplicationComponent.builder()
            .appModule(AppModule(this))
            .build()
    }

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        VKID.init(this)
    }
}

@Composable
fun getApplicationComponent(): ApplicationComponent {
    return (LocalContext.current.applicationContext as App).component
}