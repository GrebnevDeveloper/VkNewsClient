package com.grebnev.vknewsclient.di.components

import com.grebnev.vknewsclient.di.modules.AppModule
import com.grebnev.vknewsclient.di.modules.NetworkModule
import com.grebnev.vknewsclient.presentation.base.ViewModelFactory
import io.mockk.mockk
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ApplicationComponentTest {
    @Test
    fun `getViewModelFactory should return instance of ViewModelFactory`() {
        val mockAppModule = mockk<AppModule>()
        val mockNetworkModule = mockk<NetworkModule>()
        val component =
            DaggerApplicationComponent
                .builder()
                .appModule(mockAppModule)
                .networkModule(mockNetworkModule)
                .build()

        val viewModelFactory = component.getViewModelFactory()

        assertNotNull(viewModelFactory)
        assertTrue(viewModelFactory is ViewModelFactory)
    }

    @Test
    fun `getCommentsComponentFactory should return instance of CommentsComponent Factory`() {
        val mockAppModule = mockk<AppModule>()
        val mockNetworkModule = mockk<NetworkModule>()
        val component =
            DaggerApplicationComponent
                .builder()
                .appModule(mockAppModule)
                .networkModule(mockNetworkModule)
                .build()

        val commentsComponentFactory = component.getCommentsComponentFactory()

        assertNotNull(commentsComponentFactory)
        assertTrue(commentsComponentFactory is CommentsComponent.Factory)
    }
}