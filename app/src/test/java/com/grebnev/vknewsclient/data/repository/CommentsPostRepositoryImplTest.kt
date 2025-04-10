package com.grebnev.vknewsclient.data.repository

import app.cash.turbine.test
import com.grebnev.vknewsclient.core.handlers.ErrorHandler
import com.grebnev.vknewsclient.core.wrappers.ErrorType
import com.grebnev.vknewsclient.core.wrappers.ResultStatus
import com.grebnev.vknewsclient.data.mapper.NewsFeedMapper
import com.grebnev.vknewsclient.data.model.comments.CommentsResponseDto
import com.grebnev.vknewsclient.data.network.ApiService
import com.grebnev.vknewsclient.data.source.AccessTokenSource
import com.grebnev.vknewsclient.domain.entity.FeedPost
import com.grebnev.vknewsclient.domain.entity.PostComment
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
class CommentsPostRepositoryImplTest {
    private lateinit var mockApiService: ApiService
    private lateinit var mockMapper: NewsFeedMapper
    private lateinit var mockAccessToken: AccessTokenSource

    private lateinit var repository: CommentsPostRepositoryImpl
    private lateinit var retryTrigger: MutableSharedFlow<Unit>
    private lateinit var mockFeedPost: FeedPost

    @Before
    fun setUp() {
        mockApiService = mockk()
        mockMapper = mockk()
        mockAccessToken =
            mockk {
                coEvery { getAccessToken() } returns "mockToken"
            }

        retryTrigger = MutableSharedFlow(replay = 1)
        repository = CommentsPostRepositoryImpl(mockApiService, mockMapper, mockAccessToken)
        mockFeedPost =
            mockk<FeedPost> {
                every { id } returns 1L
                every { communityId } returns 123L
            }
    }

    @Test
    fun `getComments should emit Loading initially`() =
        runTest {
            coEvery { mockApiService.loadComments(any(), 123L, 1L) } returns mockk()
            coEvery { mockMapper.mapResponseToPostComment(any()) } returns emptyList()

            repository.getComments(mockFeedPost).test {
                assertEquals(ResultStatus.Initial, awaitItem())
                cancelAndIgnoreRemainingEvents()
            }
            advanceUntilIdle()
        }

    @Test
    fun `getComments should emit Success state when apiService returns valid response`() =
        runTest {
            val mockCommentsResponse = mockk<CommentsResponseDto>()
            val mockComments =
                listOf(
                    mockk<PostComment>(),
                    mockk<PostComment>(),
                )

            coEvery { mockApiService.loadComments(any(), 123L, 1L) } returns mockCommentsResponse
            coEvery { mockMapper.mapResponseToPostComment(mockCommentsResponse) } returns mockComments

            repository.getComments(mockFeedPost).test {
                assertEquals(ResultStatus.Initial, awaitItem())
                assertEquals(ResultStatus.Success(mockComments), awaitItem())
                cancelAndIgnoreRemainingEvents()
            }
            advanceUntilIdle()
        }

    @Test
    fun `getComments should emit Empty state when apiService returns empty list`() =
        runTest {
            val mockCommentsResponse = mockk<CommentsResponseDto>()
            coEvery { mockApiService.loadComments(any(), 123L, 1L) } returns mockCommentsResponse
            coEvery { mockMapper.mapResponseToPostComment(mockCommentsResponse) } returns emptyList()

            repository.getComments(mockFeedPost).test {
                assertEquals(ResultStatus.Initial, awaitItem())
                assertEquals(ResultStatus.Empty, awaitItem())
                cancelAndIgnoreRemainingEvents()
            }
            advanceUntilIdle()
        }

    @Test
    fun `retry should trigger new request and emit Success state`() =
        runTest {
            val mockCommentsResponse = mockk<CommentsResponseDto>()
            val mockComments =
                listOf(
                    mockk<PostComment>(),
                )

            coEvery { mockApiService.loadComments(any(), 123L, 1L) } returns mockCommentsResponse
            coEvery { mockMapper.mapResponseToPostComment(mockCommentsResponse) } returns mockComments

            repository.getComments(mockFeedPost).test {
                assertEquals(ResultStatus.Initial, awaitItem())
                repository.retry()
                advanceUntilIdle()
                assertEquals(ResultStatus.Success(mockComments), awaitItem())
                cancelAndIgnoreRemainingEvents()
            }
            advanceUntilIdle()

            coVerify { mockApiService.loadComments("mockToken", 123L, 1L) }
        }

    @Test
    fun `getComments should emit Error when API call fails`() =
        runTest {
            mockkObject(ErrorHandler)

            val exception = IOException("Network error")
            val errorType = ErrorType.NETWORK_ERROR

            coEvery { mockApiService.loadComments(any(), 123L, 1L) } throws exception
            every { ErrorHandler.getErrorType(exception) } returns errorType

            repository.getComments(mockFeedPost).test(timeout = 13.seconds) {
                assertEquals(ResultStatus.Initial, awaitItem())
                assertEquals(ResultStatus.Error(errorType), awaitItem())
            }
            advanceUntilIdle()

            coVerify(exactly = 4) { mockApiService.loadComments("mockToken", 123L, 1L) }
            verify(exactly = 1) { ErrorHandler.getErrorType(exception) }

            unmockkObject(ErrorHandler)
        }
}