package com.grebnev.vknewsclient.presentation.profile

import com.grebnev.vknewsclient.core.wrappers.ErrorType
import com.grebnev.vknewsclient.core.wrappers.ResultStatus
import com.grebnev.vknewsclient.domain.entity.ProfileInfo
import com.grebnev.vknewsclient.domain.usecases.GetProfileInfoUseCase
import com.grebnev.vknewsclient.presentation.base.ErrorMessageProvider
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ProfileInfoViewModelTest {
    private lateinit var mockProfileInfoUseCase: GetProfileInfoUseCase
    private lateinit var mockErrorMessageProvider: ErrorMessageProvider

    private lateinit var viewModel: ProfileInfoViewModel

    @Before
    fun setUp() {
        mockProfileInfoUseCase = mockk()
        mockErrorMessageProvider = mockk()
    }

    @Test
    fun `screenState should emit Loading initially`() =
        runTest {
            coEvery { mockProfileInfoUseCase.getProfileInfo } returns emptyFlow()

            viewModel = createViewModel()

            val result = viewModel.screenState.first { it is ProfileInfoScreenState.Loading }

            assertEquals(ProfileInfoScreenState.Loading, result)
            advanceUntilIdle()
        }

    @Test
    fun `screenState should emit Profile state when useCase returns Success`() =
        runTest {
            val mockProfileInfo =
                mockk<ProfileInfo> {
                    every { id } returns 1L
                    every { avatarUrl } returns "https://example.com/avatar.jpg"
                    every { firstName } returns "John"
                    every { lastName } returns "Doe"
                }

            coEvery { mockProfileInfoUseCase.getProfileInfo } returns
                flowOf(ResultStatus.Success(mockProfileInfo))

            viewModel = createViewModel()

            val result = viewModel.screenState.first { it is ProfileInfoScreenState.Profile }

            assertEquals(ProfileInfoScreenState.Profile(mockProfileInfo), result)
            advanceUntilIdle()
        }

    @Test
    fun `screenState should emit Error state when useCase returns Error`() =
        runTest {
            val errorType = ErrorType.NETWORK_ERROR
            val errorMessage = "Network error"
            coEvery { mockErrorMessageProvider.getErrorMessage(errorType) } returns errorMessage

            coEvery { mockProfileInfoUseCase.getProfileInfo } returns
                flowOf(ResultStatus.Error(errorType))

            viewModel = createViewModel()

            val result = viewModel.screenState.first { it is ProfileInfoScreenState.Error }

            assertEquals(ProfileInfoScreenState.Error(errorMessage), result)
            advanceUntilIdle()
        }

    @Test
    fun `refreshedProfileInfo should emit Loading and trigger retry`() =
        runTest {
            coEvery { mockProfileInfoUseCase.getProfileInfo } returns emptyFlow()
            coEvery { mockProfileInfoUseCase.retry() } just Runs

            viewModel = createViewModel()

            viewModel.refreshedProfileInfo()
            delay(100)

            val result = viewModel.screenState.first { it is ProfileInfoScreenState.Loading }

            assertEquals(ProfileInfoScreenState.Loading, result)
            advanceUntilIdle()

            coVerify { mockProfileInfoUseCase.retry() }
        }

    private fun createViewModel(): ProfileInfoViewModel =
        ProfileInfoViewModel(mockProfileInfoUseCase, mockErrorMessageProvider)
}