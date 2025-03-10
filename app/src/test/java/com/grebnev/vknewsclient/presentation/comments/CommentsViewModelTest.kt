package com.grebnev.vknewsclient.presentation.comments

import app.cash.turbine.test
import com.grebnev.vknewsclient.core.wrappers.ErrorType
import com.grebnev.vknewsclient.core.wrappers.ResultState
import com.grebnev.vknewsclient.domain.entity.FeedPost
import com.grebnev.vknewsclient.domain.entity.PostComment
import com.grebnev.vknewsclient.domain.usecases.GetCommentsUseCase
import com.grebnev.vknewsclient.presentation.base.ErrorMessageProvider
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
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
    private lateinit var commentsStateFlow: MutableStateFlow<ResultState<List<PostComment>, ErrorType>>

    private val mockFeedPost = mockk<FeedPost>()

    @Before
    fun setUp() {
        mockGetCommentsUseCase = mockk()
        mockErrorMessageProvider = mockk()

        commentsStateFlow = MutableStateFlow(ResultState.Initial)
        coEvery { mockGetCommentsUseCase.getCommentsPost(mockFeedPost) } returns commentsStateFlow

        viewModel =
            CommentsViewModel(
                feedPost = mockFeedPost,
                getCommentsUseCase = mockGetCommentsUseCase,
                errorMessage = mockErrorMessageProvider,
            )
    }

    @Test
    fun `screenState should emit Loading initially`() =
        runTest {
            commentsStateFlow.emit(ResultState.Initial)

            viewModel.screenState.test {
                assertEquals(CommentsScreenState.Loading, awaitItem())
                cancelAndIgnoreRemainingEvents()
            }
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
            commentsStateFlow.emit(ResultState.Success(mockComments))

            viewModel.screenState.test {
                assertEquals(CommentsScreenState.Comments(mockFeedPost, mockComments), awaitItem())
                cancelAndIgnoreRemainingEvents()
            }
            advanceUntilIdle()
        }

    @Test
    fun `screenState should emit NoComments state when useCase returns Empty`() =
        runTest {
            commentsStateFlow.emit(ResultState.Empty)

            viewModel.screenState.test {
                assertEquals(CommentsScreenState.NoComments, awaitItem())
                cancelAndIgnoreRemainingEvents()
            }
            advanceUntilIdle()
        }

    @Test
    fun `screenState should emit Error state when useCase returns Error`() =
        runTest {
            val errorType = ErrorType.NETWORK_ERROR
            val errorMessage = "Network error"
            coEvery { mockErrorMessageProvider.getErrorMessage(errorType) } returns errorMessage
            commentsStateFlow.emit(ResultState.Error(errorType))

            viewModel.screenState.test {
                assertEquals(CommentsScreenState.Error(errorMessage), awaitItem())
                cancelAndIgnoreRemainingEvents()
            }
            advanceUntilIdle()
        }

    @Test
    fun `refreshedCommentsPost should emit Loading and call retry`() =
        runTest {
            coEvery { mockGetCommentsUseCase.retry() } returns Unit

            viewModel.screenState.test {
                commentsStateFlow.emit(ResultState.Error(mockk()))
                viewModel.refreshedCommentsPost()
                advanceUntilIdle()
                assertEquals(CommentsScreenState.Loading, awaitItem())
                cancelAndIgnoreRemainingEvents()
            }
            advanceUntilIdle()

            coVerify { mockGetCommentsUseCase.retry() }
        }
}