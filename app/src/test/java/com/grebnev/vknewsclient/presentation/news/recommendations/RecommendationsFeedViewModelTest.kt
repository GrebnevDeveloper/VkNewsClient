package com.grebnev.vknewsclient.presentation.news.recommendations

import app.cash.turbine.test
import com.grebnev.vknewsclient.core.wrappers.ErrorType
import com.grebnev.vknewsclient.core.wrappers.ResultStatus
import com.grebnev.vknewsclient.di.keys.NewsFeedType
import com.grebnev.vknewsclient.domain.entity.FeedPost
import com.grebnev.vknewsclient.domain.usecases.ChangeLikeStatusUseCase
import com.grebnev.vknewsclient.domain.usecases.ChangeSubscriptionStatusUseCase
import com.grebnev.vknewsclient.domain.usecases.DeletePostUseCase
import com.grebnev.vknewsclient.domain.usecases.GetRecommendationsUseCase
import com.grebnev.vknewsclient.domain.usecases.LoadNextDataUseCase
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

@ExperimentalCoroutinesApi
class RecommendationsFeedViewModelTest {
    private lateinit var mockGetRecommendationsUseCase: GetRecommendationsUseCase
    private lateinit var mockLoadNextDataUseCase: LoadNextDataUseCase
    private lateinit var mockChangeLikeStatusUseCase: ChangeLikeStatusUseCase
    private lateinit var mockDeletePostUseCase: DeletePostUseCase
    private lateinit var mockChangeSubscriptionStatusUseCase: ChangeSubscriptionStatusUseCase
    private lateinit var mockErrorMessageProvider: ErrorMessageProvider

    private lateinit var viewModel: RecommendationsFeedViewModel
    private lateinit var recommendationsStateFlow: MutableStateFlow<ResultStatus<List<FeedPost>, ErrorType>>

    @Before
    fun setUp() {
        mockGetRecommendationsUseCase = mockk()
        mockLoadNextDataUseCase = mockk()
        mockChangeLikeStatusUseCase = mockk()
        mockDeletePostUseCase = mockk()
        mockChangeSubscriptionStatusUseCase = mockk()
        mockErrorMessageProvider = mockk()

        recommendationsStateFlow = MutableStateFlow(ResultStatus.Initial)
        coEvery { mockGetRecommendationsUseCase() } returns recommendationsStateFlow

        viewModel =
            RecommendationsFeedViewModel(
                getRecommendationsUseCase = mockGetRecommendationsUseCase,
                loadNextDataUseCase = mockLoadNextDataUseCase,
                changeLikeStatusUseCase = mockChangeLikeStatusUseCase,
                deletePostUseCase = mockDeletePostUseCase,
                changeSubscriptionStatusUseCase = mockChangeSubscriptionStatusUseCase,
                errorMessageProvider = mockErrorMessageProvider,
            )
    }

    @Test
    fun `screenState should emit Loading initially`() =
        runTest {
            recommendationsStateFlow.emit(ResultStatus.Initial)

            viewModel.screenState.test {
                assertEquals(RecommendationsFeedScreenState.Loading, awaitItem())
                cancelAndIgnoreRemainingEvents()
            }
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
            recommendationsStateFlow.emit(ResultStatus.Success(mockFeedPosts))

            viewModel.screenState.test {
                assertEquals(
                    RecommendationsFeedScreenState.Posts(mockFeedPosts),
                    awaitItem(),
                )
                cancelAndIgnoreRemainingEvents()
            }
            advanceUntilIdle()
        }

    @Test
    fun `screenState should emit NoRecommendations state when useCase returns Empty`() =
        runTest {
            recommendationsStateFlow.emit(ResultStatus.Empty)

            viewModel.screenState.test {
                assertEquals(RecommendationsFeedScreenState.NoRecommendations, awaitItem())
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
            recommendationsStateFlow.emit(ResultStatus.Error(errorType))

            viewModel.screenState.test {
                assertEquals(RecommendationsFeedScreenState.Error(errorMessage), awaitItem())
                cancelAndIgnoreRemainingEvents()
            }
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
            coEvery { mockLoadNextDataUseCase(NewsFeedType.RECOMMENDATIONS) } returns Unit
            recommendationsStateFlow.emit(ResultStatus.Success(mockFeedPosts))

            viewModel.loadNextPosts()
            advanceUntilIdle()

            coVerify { mockLoadNextDataUseCase(NewsFeedType.RECOMMENDATIONS) }
        }

    @Test
    fun `changeLikeStatus should call changeLikeStatusUseCase`() =
        runTest {
            val mockFeedPost = mockk<FeedPost>()
            coEvery {
                mockChangeLikeStatusUseCase(
                    mockFeedPost,
                    NewsFeedType.RECOMMENDATIONS,
                )
            } returns Unit

            viewModel.changeLikeStatus(mockFeedPost)
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
            val mockFeedPost = mockk<FeedPost>()
            coEvery {
                mockChangeSubscriptionStatusUseCase(
                    mockFeedPost,
                    NewsFeedType.RECOMMENDATIONS,
                )
            } returns Unit

            viewModel.changeSubscriptionStatus(mockFeedPost)
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
            val mockFeedPost = mockk<FeedPost>()
            coEvery { mockDeletePostUseCase(mockFeedPost, NewsFeedType.RECOMMENDATIONS) } returns Unit

            viewModel.delete(mockFeedPost)
            advanceUntilIdle()

            coVerify { mockDeletePostUseCase(mockFeedPost, NewsFeedType.RECOMMENDATIONS) }
        }
}