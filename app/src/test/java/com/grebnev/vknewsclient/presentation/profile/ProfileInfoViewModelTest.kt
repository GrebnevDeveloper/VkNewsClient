package com.grebnev.vknewsclient.presentation.profile

import app.cash.turbine.test
import com.grebnev.vknewsclient.core.wrappers.ErrorType
import com.grebnev.vknewsclient.core.wrappers.ResultState
import com.grebnev.vknewsclient.domain.entity.ProfileInfo
import com.grebnev.vknewsclient.domain.usecases.GetProfileInfoUseCase
import com.grebnev.vknewsclient.presentation.base.ErrorMessageProvider
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class ProfileInfoViewModelTest {

    private lateinit var mockProfileInfoUseCase: GetProfileInfoUseCase
    private lateinit var mockErrorMessageProvider: ErrorMessageProvider

    private lateinit var viewModel: ProfileInfoViewModel
    private lateinit var profileInfoStateFlow: MutableStateFlow<ResultState<ProfileInfo, ErrorType>>

    @Before
    fun setUp() {
        mockProfileInfoUseCase = mockk()
        mockErrorMessageProvider = mockk()
        profileInfoStateFlow = MutableStateFlow(ResultState.Initial)
        coEvery { mockProfileInfoUseCase.getProfileInfo } returns profileInfoStateFlow
        viewModel = ProfileInfoViewModel(mockProfileInfoUseCase, mockErrorMessageProvider)
    }

    @Test
    fun `screenState should emit Loading initially`() = runTest {
        profileInfoStateFlow.emit(ResultState.Initial)

        viewModel.screenState.test {
            assertEquals(ProfileInfoScreenState.Loading, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `screenState should emit Profile state when useCase returns Success`() = runTest {
        val mockProfileInfo = mockk<ProfileInfo> {
            every { id } returns 1L
            every { avatarUrl } returns "https://example.com/avatar.jpg"
            every { firstName } returns "John"
            every { lastName } returns "Doe"
        }

        profileInfoStateFlow.emit(ResultState.Success(mockProfileInfo))

        viewModel.screenState.test {
            assertEquals(ProfileInfoScreenState.Profile(mockProfileInfo), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `screenState should emit Error state when useCase returns Error`() = runTest {
        val errorType = ErrorType.NETWORK_ERROR
        val errorMessage = "Network error"
        coEvery { mockErrorMessageProvider.getErrorMessage(errorType) } returns errorMessage

        profileInfoStateFlow.emit(ResultState.Error(errorType))

        viewModel.screenState.test {
            assertEquals(ProfileInfoScreenState.Error(errorMessage), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `refreshedProfileInfo should emit Loading and trigger retry`() = runTest {
        coEvery { mockProfileInfoUseCase.retry() } returns Unit

        viewModel.screenState.test {
            profileInfoStateFlow.emit(ResultState.Error(mockk()))
            viewModel.refreshedProfileInfo()
            advanceUntilIdle()
            assertEquals(ProfileInfoScreenState.Loading, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }

        coVerify(exactly = 1) { mockProfileInfoUseCase.retry() }
    }
}