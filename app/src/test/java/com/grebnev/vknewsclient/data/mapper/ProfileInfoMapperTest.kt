package com.grebnev.vknewsclient.data.mapper

import com.grebnev.vknewsclient.data.model.profile.ProfileInfoDto
import com.grebnev.vknewsclient.data.model.profile.ProfileInfoResponseDto
import io.mockk.every
import io.mockk.mockk
import org.junit.Test
import kotlin.test.assertEquals

class ProfileInfoMapperTest {
    private val profileInfoMapper = ProfileInfoMapper()

    @Test
    fun `mapResponseToProfileInfo should map ProfileInfoResponseDto to ProfileInfo`() {
        val profile= mockk<ProfileInfoDto> {
            every { id } returns 1L
            every { avatarUrl } returns "https://example.com/avatar.jpg"
            every { firstName } returns "John"
            every { lastName } returns "Doe"
        }
        val response = mockk<ProfileInfoResponseDto> {
            every { profileInfo } returns profile
        }

        val result = profileInfoMapper.mapResponseToProfileInfo(response)

        assertEquals(1L, result.id)
        assertEquals("https://example.com/avatar.jpg", result.avatarUrl)
        assertEquals("John", result.firstName)
        assertEquals("Doe", result.lastName)
    }
}