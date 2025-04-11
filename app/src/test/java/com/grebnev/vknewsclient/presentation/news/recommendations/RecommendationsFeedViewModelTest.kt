package com.grebnev.vknewsclient.presentation.news.recommendations

import com.grebnev.vknewsclient.core.wrappers.ErrorType
import com.grebnev.vknewsclient.core.wrappers.ResultStatus
import com.grebnev.vknewsclient.di.keys.NewsFeedType
import com.grebnev.vknewsclient.domain.entity.FeedPost
import com.grebnev.vknewsclient.domain.usecases.ChangeLikeStatusUseCase
import com.grebnev.vknewsclient.domain.usecases.ChangeSubscriptionStatusUseCase
import com.grebnev.vknewsclient.domain.usecases.DeletePostUseCase
import com.grebnev.vknewsclient.domain.usecases.GetRecommendationsUseCase
import com.grebnev.vknewsclient.domain.usecases.HasNextDataLoadingUseCase
import com.grebnev.vknewsclient.domain.usecases.LoadNextDataUseCase
import com.grebnev.vknewsclient.presentation.base.ErrorMessageProvider
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class RecommendationsFeedViewModelTest {
    private lateinit var mockGetRecommendationsUseCase: GetRecommendationsUseCase
    private lateinit var mockLoadNextDataUseCase: LoadNextDataUseCase
    private lateinit var mockChangeLikeStatusUseCase: ChangeLikeStatusUseCase
    private lateinit var mockDeletePostUseCase: DeletePostUseCase
    private lateinit var mockChangeSubscriptionStatusUseCase: ChangeSubscriptionStatusUseCase
    private lateinit var mockErrorMessageProvider: ErrorMessageProvider
    private lateinit var mockHasNextDataLoadingUseCase: HasNextDataLoadingUseCase

    private lateinit var viewModel: RecommendationsFeedViewModel

    @Before
    fun setUp() {
        mockGetRecommendationsUseCase = mockk()
        mockLoadNextDataUseCase = mockk()
        mockChangeLikeStatusUseCase = mockk()
        mockDeletePostUseCase = mockk()
        mockChangeSubscriptionStatusUseCase = mockk()
        mockErrorMessageProvider = mockk()
        mockHasNextDataLoadingUseCase =
            mockk {
                every { this@mockk.invoke(any()) } returns MutableStateFlow(false)
            }
    }

    @Test
    fun `screenState should emit Loading initially`() =
        runTest {
            coEvery { mockGetRecommendationsUseCase() } returns emptyFlow()

            viewModel = createViewModel()

            val result = viewModel.screenState.first { it is RecommendationsFeedScreenState.Loading }

            assertEquals(RecommendationsFeedScreenState.Loading, result)
            advanceUntilIdle()
        }

    @Test
    fun `screenState should emit Posts state when useCase returns Success with non-empty list`() =
        runTest {
            val mockFeedPosts =
                listOf(
                    mockk<FeedPost> {
                        every { id } returns 1L
                        every { contentText } returns "Post 1"
                    },
                    mockk<FeedPost> {
                        every { id } returns 2L
                        every { contentText } returns "Post 2"
                    },
                )
            coEvery { mockGetRecommendationsUseCase() } returns flowOf(ResultStatus.Success(mockFeedPosts))

            viewModel = createViewModel()

            val result = viewModel.screenState.first { it is RecommendationsFeedScreenState.Posts }

            assertEquals(RecommendationsFeedScreenState.Posts(mockFeedPosts), result)
            advanceUntilIdle()
        }

    @Test
    fun `screenState should emit NoRecommendations state when useCase returns Empty`() =
        runTest {
            coEvery { mockGetRecommendationsUseCase() } returns flowOf(ResultStatus.Empty)

            viewModel = createViewModel()

            val result =
                viewModel.screenState.first { it is RecommendationsFeedScreenState.NoRecommendations }

            assertEquals(RecommendationsFeedScreenState.NoRecommendations, result)
            advanceUntilIdle()
        }

    @Test
    fun `screenState should emit Error state when useCase returns Error`() =
        runTest {
            val errorType = ErrorType.NETWORK_ERROR
            val errorMessage = "Network error"
            coEvery { mockErrorMessageProvider.getErrorMessage(errorType) } returns errorMessage
            coEvery { mockGetRecommendationsUseCase() } returns flowOf(ResultStatus.Error(errorType))

            viewModel = createViewModel()

            val result = viewModel.screenState.first { it is RecommendationsFeedScreenState.Error }

            assertEquals(RecommendationsFeedScreenState.Error(errorMessage), result)
            advanceUntilIdle()
        }

    @Test
    fun `loadNextPosts should call loadNextDataUseCase`() =
        runTest {
            val mockFeedPosts =
                listOf(
                    mockk<FeedPost> {
                        every { id } returns 1L
                        every { contentText } returns "Post 1"
                    },
                )
            coEvery { mockLoadNextDataUseCase(NewsFeedType.RECOMMENDATIONS) } just Runs
            coEvery { mockGetRecommendationsUseCase() } returns flowOf(ResultStatus.Success(mockFeedPosts))

            viewModel = createViewModel()

            viewModel.loadNextPosts()
            delay(100)
            advanceUntilIdle()

            coVerify { mockLoadNextDataUseCase(NewsFeedType.RECOMMENDATIONS) }
        }

    @Test
    fun `changeLikeStatus should call changeLikeStatusUseCase`() =
        runTest {
            coEvery { mockGetRecommendationsUseCase() } returns flowOf(ResultStatus.Success(emptyList()))
            val mockFeedPost = mockk<FeedPost>()
            coEvery {
                mockChangeLikeStatusUseCase(
                    mockFeedPost,
                    NewsFeedType.RECOMMENDATIONS,
                )
            } just Runs

            viewModel = createViewModel()

            viewModel.changeLikeStatus(mockFeedPost)
            delay(100)
            advanceUntilIdle()

            coVerify {
                mockChangeLikeStatusUseCase(
                    mockFeedPost,
                    NewsFeedType.RECOMMENDATIONS,
                )
            }
        }

    @Test
    fun `changeSubscriptionStatus should call changeSubscriptionStatusUseCase`() =
        runTest {
            coEvery { mockGetRecommendationsUseCase() } returns flowOf(ResultStatus.Success(emptyList()))
            val mockFeedPost = mockk<FeedPost>()
            coEvery {
                mockChangeSubscriptionStatusUseCase(
                    mockFeedPost,
                    NewsFeedType.RECOMMENDATIONS,
                )
            } just Runs

            viewModel = createViewModel()

            viewModel.changeSubscriptionStatus(mockFeedPost)
            delay(100)
            advanceUntilIdle()

            coVerify {
                mockChangeSubscriptionStatusUseCase(
                    mockFeedPost,
                    NewsFeedType.RECOMMENDATIONS,
                )
            }
        }

    @Test
    fun `delete should call deletePostUseCase`() =
        runTest {
            coEvery { mockGetRecommendationsUseCase() } returns flowOf(ResultStatus.Success(emptyList()))
            val mockFeedPost = mockk<FeedPost>()
            coEvery { mockDeletePostUseCase(mockFeedPost, NewsFeedType.RECOMMENDATIONS) } just Runs

            viewModel = createViewModel()

            viewModel.delete(mockFeedPost)
            delay(100)
            advanceUntilIdle()

            coVerify { mockDeletePostUseCase(mockFeedPost, NewsFeedType.RECOMMENDATIONS) }
        }

    private fun createViewModel(): RecommendationsFeedViewModel =
        RecommendationsFeedViewModel(
            getRecommendationsUseCase = mockGetRecommendationsUseCase,
            loadNextDataUseCase = mockLoadNextDataUseCase,
            changeLikeStatusUseCase = mockChangeLikeStatusUseCase,
            deletePostUseCase = mockDeletePostUseCase,
            changeSubscriptionStatusUseCase = mockChangeSubscriptionStatusUseCase,
            errorMessageProvider = mockErrorMessageProvider,
            hasNextDataLoadingUseCase = mockHasNextDataLoadingUseCase,
        )
}