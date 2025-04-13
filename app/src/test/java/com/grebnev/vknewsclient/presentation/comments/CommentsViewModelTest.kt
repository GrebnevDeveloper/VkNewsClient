package com.grebnev.vknewsclient.presentation.comments

import com.grebnev.vknewsclient.core.wrappers.ErrorType
import com.grebnev.vknewsclient.core.wrappers.ResultStatus
import com.grebnev.vknewsclient.domain.entity.FeedPost
import com.grebnev.vknewsclient.domain.entity.PostComment
import com.grebnev.vknewsclient.domain.usecases.GetCommentsUseCase
import com.grebnev.vknewsclient.presentation.base.ErrorMessageProvider
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
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

@ExperimentalCoroutinesApi
class CommentsViewModelTest {
    private lateinit var mockGetCommentsUseCase: GetCommentsUseCase
    private lateinit var mockErrorMessageProvider: ErrorMessageProvider

    private lateinit var viewModel: CommentsViewModel

    private val mockFeedPost = mockk<FeedPost>()

    @Before
    fun setUp() {
        mockGetCommentsUseCase = mockk()
        mockErrorMessageProvider = mockk()
    }

    @Test
    fun `screenState should emit Loading initially`() =
        runTest {
            coEvery { mockGetCommentsUseCase.getCommentsPost(mockFeedPost) } returns emptyFlow()

            viewModel = createViewModel()

            val result = viewModel.screenState.first { it is CommentsScreenState.Loading }

            assertEquals(CommentsScreenState.Loading, result)
            advanceUntilIdle()
        }

    @Test
    fun `screenState should emit Comments state when useCase returns Success`() =
        runTest {
            val mockComments =
                listOf(
                    mockk<PostComment>(),
                    mockk<PostComment>(),
                )
            coEvery { mockGetCommentsUseCase.getCommentsPost(mockFeedPost) } returns
                flowOf(ResultStatus.Success(mockComments))

            viewModel = createViewModel()

            val result = viewModel.screenState.first { it is CommentsScreenState.Comments }

            assertEquals(CommentsScreenState.Comments(mockFeedPost, mockComments), result)
            advanceUntilIdle()
        }

    @Test
    fun `screenState should emit NoComments state when useCase returns Empty`() =
        runTest {
            coEvery { mockGetCommentsUseCase.getCommentsPost(mockFeedPost) } returns
                flowOf(ResultStatus.Empty)

            viewModel = createViewModel()

            val result = viewModel.screenState.first { it is CommentsScreenState.NoComments }

            assertEquals(CommentsScreenState.NoComments, result)
            advanceUntilIdle()
        }

    @Test
    fun `screenState should emit Error state when useCase returns Error`() =
        runTest {
            val errorType = ErrorType.NETWORK_ERROR
            val errorMessage = "Network error"
            coEvery { mockErrorMessageProvider.getErrorMessage(errorType) } returns errorMessage
            coEvery { mockGetCommentsUseCase.getCommentsPost(mockFeedPost) } returns
                flowOf(ResultStatus.Error(errorType))

            viewModel = createViewModel()

            val result = viewModel.screenState.first { it is CommentsScreenState.Error }

            assertEquals(CommentsScreenState.Error(errorMessage), result)
            advanceUntilIdle()
        }

    @Test
    fun `refreshedCommentsPost should emit Loading and call retry`() =
        runTest {
            coEvery { mockGetCommentsUseCase.getCommentsPost(mockFeedPost) } returns emptyFlow()
            coEvery { mockGetCommentsUseCase.retry() } just Runs

            viewModel = createViewModel()

            viewModel.refreshedCommentsPost()
            delay(100)

            val result = viewModel.screenState.first { it is CommentsScreenState.Loading }

            assertEquals(CommentsScreenState.Loading, result)
            advanceUntilIdle()

            coVerify { mockGetCommentsUseCase.retry() }
        }

    private fun createViewModel(): CommentsViewModel =
        CommentsViewModel(
            feedPost = mockFeedPost,
            getCommentsUseCase = mockGetCommentsUseCase,
            errorMessage = mockErrorMessageProvider,
        )
}