package com.grebnev.vknewsclient.presentation

import android.app.Application
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.grebnev.vknewsclient.di.components.ApplicationComponent
import com.grebnev.vknewsclient.di.components.DaggerApplicationComponent
import com.vk.id.VKID

class App : Application() {

    val component: ApplicationComponent by lazy {
        DaggerApplicationComponent.create()
    }

    override fun onCreate() {
        super.onCreate()
        VKID.init(this)
    }
}

@Composable
fun getApplicationComponent(): ApplicationComponent {
    return (LocalContext.current.applicationContext as App).component
}