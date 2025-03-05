package com.grebnev.vknewsclient.data.source

import com.vk.id.AccessToken
import com.vk.id.VKID
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkAll
import org.junit.Assert.assertEquals
import org.junit.Test

class AccessTokenSourceTest {

    @Test
    fun `getAccessToken should return token when VKID provides a valid access token`() {
        mockkObject(VKID)
        val mockAccessToken = mockk<AccessToken> {
            every { token } returns "mockToken"
        }
        every { VKID.instance.accessToken } returns mockAccessToken
        val accessTokenSource = AccessTokenSource()

        val token = accessTokenSource.getAccessToken()

        assertEquals("mockToken", token)

        unmockkAll()
    }

    @Test(expected = IllegalStateException::class)
    fun `getAccessToken should throw IllegalStateException when VKID access token is null`() {
        mockkObject(VKID)
        every { VKID.instance.accessToken } returns null
        val accessTokenSource = AccessTokenSource()

        accessTokenSource.getAccessToken()

        unmockkAll()
    }
}