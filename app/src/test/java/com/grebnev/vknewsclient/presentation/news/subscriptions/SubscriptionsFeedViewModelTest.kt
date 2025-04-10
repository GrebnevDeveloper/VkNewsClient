package com.grebnev.vknewsclient.presentation.news.subscriptions

import app.cash.turbine.test
import com.grebnev.vknewsclient.core.wrappers.ErrorType
import com.grebnev.vknewsclient.core.wrappers.ResultStatus
import com.grebnev.vknewsclient.di.keys.NewsFeedType
import com.grebnev.vknewsclient.domain.entity.FeedPost
import com.grebnev.vknewsclient.domain.usecases.ChangeLikeStatusUseCase
import com.grebnev.vknewsclient.domain.usecases.ChangeSubscriptionStatusUseCase
import com.grebnev.vknewsclient.domain.usecases.DeletePostUseCase
import com.grebnev.vknewsclient.domain.usecases.GetSubscriptionPostsUseCase
import com.grebnev.vknewsclient.domain.usecases.LoadNextDataUseCase
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

@OptIn(ExperimentalCoroutinesApi::class)
class SubscriptionsFeedViewModelTest {
    private lateinit var mockGetSubscriptionPostsUseCase: GetSubscriptionPostsUseCase
    private lateinit var mockLoadNextDataUseCase: LoadNextDataUseCase
    private lateinit var mockChangeLikeStatusUseCase: ChangeLikeStatusUseCase
    private lateinit var mockDeletePostUseCase: DeletePostUseCase
    private lateinit var mockChangeSubscriptionStatusUseCase: ChangeSubscriptionStatusUseCase
    private lateinit var mockErrorMessageProvider: ErrorMessageProvider

    private lateinit var viewModel: SubscriptionsFeedViewModel
    private lateinit var subscriptionsStateFlow: MutableStateFlow<ResultStatus<List<FeedPost>, ErrorType>>

    @Before
    fun setUp() {
        mockGetSubscriptionPostsUseCase = mockk()
        mockLoadNextDataUseCase = mockk()
        mockChangeLikeStatusUseCase = mockk()
        mockDeletePostUseCase = mockk()
        mockChangeSubscriptionStatusUseCase = mockk()
        mockErrorMessageProvider = mockk()

        subscriptionsStateFlow = MutableStateFlow(ResultStatus.Initial)
        coEvery { mockGetSubscriptionPostsUseCase() } returns subscriptionsStateFlow

        viewModel =
            SubscriptionsFeedViewModel(
                getSubscriptionPostsUseCase = mockGetSubscriptionPostsUseCase,
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
            subscriptionsStateFlow.emit(ResultStatus.Initial)

            viewModel.screenState.test {
                assertEquals(SubscriptionsScreenState.Loading, awaitItem())
                cancelAndIgnoreRemainingEvents()
            }
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
            subscriptionsStateFlow.emit(ResultStatus.Success(mockFeedPosts))

            viewModel.screenState.test {
                assertEquals(SubscriptionsScreenState.Posts(mockFeedPosts), awaitItem())
                cancelAndIgnoreRemainingEvents()
            }
            advanceUntilIdle()
        }

    @Test
    fun `screenState should emit NoSubscriptions state when useCase returns Empty`() =
        runTest {
            subscriptionsStateFlow.emit(ResultStatus.Empty)

            viewModel.screenState.test {
                assertEquals(SubscriptionsScreenState.NoSubscriptions, awaitItem())
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

            subscriptionsStateFlow.emit(ResultStatus.Error(errorType))

            viewModel.screenState.test {
                assertEquals(SubscriptionsScreenState.Error(errorMessage), awaitItem())
                cancelAndIgnoreRemainingEvents()
            }
            advanceUntilIdle()
        }

    @Test
    fun `loadNextPosts should call loadNextDataUseCase`() =
        runTest {
            val mockFeedPosts =
                listOf(
                    mockk<FeedPost>(),
                )
            subscriptionsStateFlow.emit(ResultStatus.Success(mockFeedPosts))
            coEvery { mockLoadNextDataUseCase(NewsFeedType.SUBSCRIPTIONS) } returns Unit

            viewModel.loadNextPosts()
            advanceUntilIdle()

            coVerify { mockLoadNextDataUseCase(NewsFeedType.SUBSCRIPTIONS) }
        }

    @Test
    fun `changeLikeStatus should call changeLikeStatusUseCase`() =
        runTest {
            val mockFeedPost = mockk<FeedPost>()
            coEvery {
                mockChangeLikeStatusUseCase(
                    mockFeedPost,
                    NewsFeedType.SUBSCRIPTIONS,
                )
            } returns Unit

            viewModel.changeLikeStatus(mockFeedPost)
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
            val mockFeedPost = mockk<FeedPost>()
            coEvery {
                mockChangeSubscriptionStatusUseCase(
                    mockFeedPost,
                    NewsFeedType.SUBSCRIPTIONS,
                )
            } returns Unit

            viewModel.changeSubscriptionStatus(mockFeedPost)
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
            val mockFeedPost = mockk<FeedPost>()
            coEvery { mockDeletePostUseCase(mockFeedPost, NewsFeedType.SUBSCRIPTIONS) } returns Unit

            viewModel.delete(mockFeedPost)
            advanceUntilIdle()

            coVerify { mockDeletePostUseCase(mockFeedPost, NewsFeedType.SUBSCRIPTIONS) }
        }
}