package com.grebnev.vknewsclient.di.modules

import com.grebnev.vknewsclient.data.network.ApiFactory
import com.grebnev.vknewsclient.data.network.ApiService
import com.grebnev.vknewsclient.data.source.AccessTokenSource
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkAll
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class NetworkModuleTest {
    @Test
    fun `provideApiService should return ApiService instance from ApiFactory`() {
        mockkObject(ApiFactory)
        val mockApiService = mockk<ApiService>()
        every { ApiFactory.apiService } returns mockApiService
        val networkModule = NetworkModule()

        val apiService = networkModule.provideApiService()

        assertEquals(mockApiService, apiService)

        unmockkAll()
    }

    @Test
    fun `provideAccessToken should return AccessTokenSource instance`() {
        val networkModule = NetworkModule()

        val accessTokenSource = networkModule.provideAccessToken()

        assertNotNull(accessTokenSource)
        assertTrue(accessTokenSource is AccessTokenSource)
    }
}