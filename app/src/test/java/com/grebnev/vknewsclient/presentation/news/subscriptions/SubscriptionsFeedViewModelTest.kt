package com.grebnev.vknewsclient.presentation.news.subscriptions

import com.grebnev.vknewsclient.core.wrappers.ErrorType
import com.grebnev.vknewsclient.core.wrappers.ResultStatus
import com.grebnev.vknewsclient.di.keys.NewsFeedType
import com.grebnev.vknewsclient.domain.entity.FeedPost
import com.grebnev.vknewsclient.domain.usecases.ChangeLikeStatusUseCase
import com.grebnev.vknewsclient.domain.usecases.ChangeSubscriptionStatusUseCase
import com.grebnev.vknewsclient.domain.usecases.DeletePostUseCase
import com.grebnev.vknewsclient.domain.usecases.GetSubscriptionPostsUseCase
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

@OptIn(ExperimentalCoroutinesApi::class)
class SubscriptionsFeedViewModelTest {
    private lateinit var mockGetSubscriptionPostsUseCase: GetSubscriptionPostsUseCase
    private lateinit var mockLoadNextDataUseCase: LoadNextDataUseCase
    private lateinit var mockChangeLikeStatusUseCase: ChangeLikeStatusUseCase
    private lateinit var mockDeletePostUseCase: DeletePostUseCase
    private lateinit var mockChangeSubscriptionStatusUseCase: ChangeSubscriptionStatusUseCase
    private lateinit var mockErrorMessageProvider: ErrorMessageProvider
    private lateinit var mockHasNextDataLoadingUseCase: HasNextDataLoadingUseCase

    private lateinit var viewModel: SubscriptionsFeedViewModel

    @Before
    fun setUp() {
        mockGetSubscriptionPostsUseCase = mockk()
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
            coEvery { mockGetSubscriptionPostsUseCase() } returns emptyFlow()

            viewModel = createViewModel()

            val result = viewModel.screenState.first { it is SubscriptionsScreenState.Loading }

            assertEquals(SubscriptionsScreenState.Loading, result)
            advanceUntilIdle()
        }

    @Test
    fun `screenState should emit Posts state when useCase returns Success with non-empty list`() =
        runTest {
            val mockFeedPosts =
                listOf(
                    mockk<FeedPost>(),
                    mockk<FeedPost>(),
                )
            coEvery { mockGetSubscriptionPostsUseCase() } returns flowOf(ResultStatus.Success(mockFeedPosts))

            viewModel = createViewModel()

            val result = viewModel.screenState.first { it is SubscriptionsScreenState.Posts }

            assertEquals(SubscriptionsScreenState.Posts(mockFeedPosts), result)
            advanceUntilIdle()
        }

    @Test
    fun `screenState should emit NoSubscriptions state when useCase returns Empty`() =
        runTest {
            coEvery { mockGetSubscriptionPostsUseCase() } returns flowOf(ResultStatus.Empty)

            viewModel = createViewModel()

            val result = viewModel.screenState.first { it is SubscriptionsScreenState.NoSubscriptions }

            assertEquals(SubscriptionsScreenState.NoSubscriptions, result)
            advanceUntilIdle()
        }

    @Test
    fun `screenState should emit Error state when useCase returns Error`() =
        runTest {
            val errorType = ErrorType.NETWORK_ERROR
            val errorMessage = "Network error"
            coEvery { mockErrorMessageProvider.getErrorMessage(errorType) } returns errorMessage
            coEvery { mockGetSubscriptionPostsUseCase() } returns flowOf(ResultStatus.Error(errorType))

            viewModel = createViewModel()

            val result = viewModel.screenState.first { it is SubscriptionsScreenState.Error }

            assertEquals(SubscriptionsScreenState.Error(errorMessage), result)
            advanceUntilIdle()
        }

    @Test
    fun `loadNextPosts should call loadNextDataUseCase`() =
        runTest {
            coEvery { mockGetSubscriptionPostsUseCase() } returns emptyFlow()
            val mockFeedPosts =
                listOf(
                    mockk<FeedPost>(),
                )
            coEvery { mockGetSubscriptionPostsUseCase() } returns flowOf(ResultStatus.Success(mockFeedPosts))
            coEvery { mockLoadNextDataUseCase(NewsFeedType.SUBSCRIPTIONS) } just Runs

            viewModel = createViewModel()

            viewModel.loadNextPosts()
            delay(100)
            advanceUntilIdle()

            coVerify { mockLoadNextDataUseCase(NewsFeedType.SUBSCRIPTIONS) }
        }

    @Test
    fun `changeLikeStatus should call changeLikeStatusUseCase`() =
        runTest {
            coEvery { mockGetSubscriptionPostsUseCase() } returns emptyFlow()
            val mockFeedPost = mockk<FeedPost>()
            coEvery {
                mockChangeLikeStatusUseCase(
                    mockFeedPost,
                    NewsFeedType.SUBSCRIPTIONS,
                )
            } just Runs

            viewModel = createViewModel()

            viewModel.changeLikeStatus(mockFeedPost)
            delay(100)
            advanceUntilIdle()

            coVerify {
                mockChangeLikeStatusUseCase(
                    mockFeedPost,
                    NewsFeedType.SUBSCRIPTIONS,
                )
            }
        }

    @Test
    fun `changeSubscriptionStatus should call changeSubscriptionStatusUseCase`() =
        runTest {
            coEvery { mockGetSubscriptionPostsUseCase() } returns emptyFlow()
            val mockFeedPost = mockk<FeedPost>()
            coEvery {
                mockChangeSubscriptionStatusUseCase(
                    mockFeedPost,
                    NewsFeedType.SUBSCRIPTIONS,
                )
            } just Runs

            viewModel = createViewModel()

            viewModel.changeSubscriptionStatus(mockFeedPost)
            delay(100)
            advanceUntilIdle()

            coVerify {
                mockChangeSubscriptionStatusUseCase(
                    mockFeedPost,
                    NewsFeedType.SUBSCRIPTIONS,
                )
            }
        }

    @Test
    fun `delete should call deletePostUseCase`() =
        runTest {
            coEvery { mockGetSubscriptionPostsUseCase() } returns emptyFlow()
            val mockFeedPost = mockk<FeedPost>()
            coEvery { mockDeletePostUseCase(mockFeedPost, NewsFeedType.SUBSCRIPTIONS) } just Runs

            viewModel = createViewModel()

            viewModel.delete(mockFeedPost)
            delay(100)
            advanceUntilIdle()

            coVerify { mockDeletePostUseCase(mockFeedPost, NewsFeedType.SUBSCRIPTIONS) }
        }

    private fun createViewModel(): SubscriptionsFeedViewModel =
        SubscriptionsFeedViewModel(
            getSubscriptionPostsUseCase = mockGetSubscriptionPostsUseCase,
            loadNextDataUseCase = mockLoadNextDataUseCase,
            changeLikeStatusUseCase = mockChangeLikeStatusUseCase,
            deletePostUseCase = mockDeletePostUseCase,
            changeSubscriptionStatusUseCase = mockChangeSubscriptionStatusUseCase,
            errorMessageProvider = mockErrorMessageProvider,
            hasNextDataLoadingUseCase = mockHasNextDataLoadingUseCase,
        )
}