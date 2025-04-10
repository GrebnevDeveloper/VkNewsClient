package com.grebnev.vknewsclient.data.repository

import app.cash.turbine.test
import com.grebnev.vknewsclient.core.handlers.ErrorHandler
import com.grebnev.vknewsclient.core.wrappers.ErrorType
import com.grebnev.vknewsclient.core.wrappers.ResultStatus
import com.grebnev.vknewsclient.data.mapper.ProfileInfoMapper
import com.grebnev.vknewsclient.data.model.profile.ProfileInfoResponseDto
import com.grebnev.vknewsclient.data.network.ApiService
import com.grebnev.vknewsclient.data.source.AccessTokenSource
import com.grebnev.vknewsclient.domain.entity.ProfileInfo
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkObject
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.io.IOException
import kotlin.time.Duration.Companion.seconds

@ExperimentalCoroutinesApi
class ProfileInfoRepositoryImplTest {
    private lateinit var mockApiService: ApiService
    private lateinit var mockMapper: ProfileInfoMapper
    private lateinit var mockAccessToken: AccessTokenSource

    private lateinit var repository: ProfileInfoRepositoryImpl
    private lateinit var retryTrigger: MutableSharedFlow<Unit>

    @Before
    fun setUp() {
        mockApiService = mockk()
        mockMapper = mockk()
        mockAccessToken =
            mockk {
                coEvery { getAccessToken() } returns "mockToken"
            }

        retryTrigger = MutableSharedFlow(replay = 1)
        repository = ProfileInfoRepositoryImpl(mockApiService, mockMapper, mockAccessToken)
    }

    @Test
    fun `getProfileInfo should emit Loading initially`() =
        runTest {
            coEvery { mockApiService.loadProfileInfo(any()) } returns mockk()
            coEvery { mockMapper.mapResponseToProfileInfo(any()) } returns mockk()

            repository.getProfileInfo.test {
                assertEquals(ResultStatus.Initial, awaitItem())
                cancelAndIgnoreRemainingEvents()
            }
            advanceUntilIdle()
        }

    @Test
    fun `getProfileInfo should emit Success state when apiService returns valid response`() =
        runTest {
            val mockProfileInfoResponse = mockk<ProfileInfoResponseDto>()
            val mockProfileInfo =
                mockk<ProfileInfo> {
                    every { id } returns 1L
                    every { avatarUrl } returns "https://example.com/avatar.jpg"
                    every { firstName } returns "John"
                    every { lastName } returns "Doe"
                }

            coEvery { mockApiService.loadProfileInfo(any()) } returns mockProfileInfoResponse
            coEvery { mockMapper.mapResponseToProfileInfo(mockProfileInfoResponse) } returns mockProfileInfo

            repository.getProfileInfo.test {
                assertEquals(ResultStatus.Initial, awaitItem())
                assertEquals(ResultStatus.Success(mockProfileInfo), awaitItem())
                cancelAndIgnoreRemainingEvents()
            }
            advanceUntilIdle()
        }

    @Test
    fun `retry should trigger new request and emit Success state`() =
        runTest {
            val mockProfileInfoResponse = mockk<ProfileInfoResponseDto>()
            val mockProfileInfo =
                mockk<ProfileInfo> {
                    every { id } returns 1L
                    every { avatarUrl } returns "https://example.com/avatar.jpg"
                    every { firstName } returns "John"
                    every { lastName } returns "Doe"
                }

            coEvery { mockApiService.loadProfileInfo(any()) } returns mockProfileInfoResponse
            coEvery { mockMapper.mapResponseToProfileInfo(mockProfileInfoResponse) } returns mockProfileInfo

            repository.getProfileInfo.test {
                assertEquals(ResultStatus.Initial, awaitItem())
                repository.retry()
                advanceUntilIdle()
                assertEquals(ResultStatus.Success(mockProfileInfo), awaitItem())
                cancelAndIgnoreRemainingEvents()
            }
            advanceUntilIdle()

            coVerify { mockApiService.loadProfileInfo("mockToken") }
        }

    @Test
    fun `getProfileInfo should emit Error when API call fails`() =
        runTest {
            mockkObject(ErrorHandler)

            val exception = IOException("Network error")
            val errorType = ErrorType.NETWORK_ERROR

            coEvery { mockApiService.loadProfileInfo(any()) } throws exception
            every { ErrorHandler.getErrorType(exception) } returns errorType

            repository.getProfileInfo.test(timeout = 13.seconds) {
                assertEquals(ResultStatus.Initial, awaitItem())
                assertEquals(ResultStatus.Error(errorType), awaitItem())
            }
            advanceUntilIdle()

            coVerify(exactly = 4) { mockApiService.loadProfileInfo("mockToken") }
            verify(exactly = 1) { ErrorHandler.getErrorType(exception) }

            unmockkObject(ErrorHandler)
        }
}