package com.grebnev.vknewsclient.di.modules

import android.app.Application
import android.content.Context
import com.grebnev.vknewsclient.presentation.base.ErrorMessageProvider
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test


class AppModuleTest {
    @Test
    fun `provideApplication should return the application instance passed to AppModule`() {
        val mockApplication = mockk<Application>()
        val appModule = AppModule(mockApplication)

        val providedApplication = appModule.provideApplication()

        assertEquals(mockApplication, providedApplication)
    }

    @Test
    fun `provideApplicationContext should return the app context from the app instance`() {
        val mockApplication = mockk<Application>()
        val mockContext = mockk<Context>()
        every { mockApplication.applicationContext } returns mockContext
        val appModule = AppModule(mockApplication)

        val providedContext = appModule.provideApplicationContext()

        assertEquals(mockContext, providedContext)
    }

    @Test
    fun `provideErrorMessage should return an instance of ErrorMessageProvider`() {
        val mockContext = mockk<Context>()
        val appModule = AppModule(mockk())

        val errorMessageProvider = appModule.provideErrorMessage(mockContext)

        assertNotNull(errorMessageProvider)
        assertTrue(errorMessageProvider is ErrorMessageProvider)
    }
}