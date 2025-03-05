package com.grebnev.vknewsclient.data.repository

import app.cash.turbine.test
import com.grebnev.vknewsclient.core.wrappers.ErrorType
import com.grebnev.vknewsclient.core.wrappers.ResultState
import com.grebnev.vknewsclient.data.network.ApiService
import com.grebnev.vknewsclient.data.source.AccessTokenSource
import com.grebnev.vknewsclient.data.source.FeedPostSource
import com.grebnev.vknewsclient.data.source.LikesStatusSource
import com.grebnev.vknewsclient.data.source.SubscriptionsStatusSource
import com.grebnev.vknewsclient.domain.entity.FeedPost
import com.grebnev.vknewsclient.domain.entity.Subscription
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
import java.io.IOException
import kotlin.time.Duration.Companion.seconds

@ExperimentalCoroutinesApi
class SubscriptionsFeedRepositoryImplTest {

    private lateinit var mockApiService: ApiService
    private lateinit var mockFeedPostSource: FeedPostSource
    private lateinit var mockLikesSource: LikesStatusSource
    private lateinit var mockSubscriptionsSource: SubscriptionsStatusSource
    private lateinit var mockAccessToken: AccessTokenSource

    private lateinit var repository: SubscriptionsFeedRepositoryImpl

    private lateinit var nextFromState: MutableStateFlow<String?>
    private lateinit var subscriptionsState: MutableStateFlow<Subscription>
    private lateinit var mockFeedPost: FeedPost

    @Before
    fun setUp() {
        mockApiService = mockk()
        mockFeedPostSource = mockk()
        mockLikesSource = mockk()
        mockSubscriptionsSource = mockk()
        mockAccessToken = mockk {
            coEvery { getAccessToken() } returns "mockToken"
        }

        nextFromState = MutableStateFlow(null)
        subscriptionsState = MutableStateFlow(
            Subscription(
                id = 1L,
                title = "SubscriptionsVkNews",
                sourceIds = setOf(-123, 456)
            )
        )
        mockFeedPost = FeedPost(
            id = 1L,
            communityId = 123L,
            communityName = "Community 1",
            publicationDate = "2024-10-01",
            communityImageUrl = "https://example.com/community.jpg",
            contentText = "Post content",
            contentImageUrl = "https://example.com/post.jpg",
            statisticsList = emptyList(),
            isLiked = false,
            isSubscribed = true
        )


        coEvery { mockFeedPostSource.nextFromState } returns nextFromState
        coEvery { mockSubscriptionsSource.getSubscriptionsState() } returns subscriptionsState

        repository = SubscriptionsFeedRepositoryImpl(
            apiService = mockApiService,
            feedPostSource = mockFeedPostSource,
            likesSource = mockLikesSource,
            subscriptionsSource = mockSubscriptionsSource,
            accessToken = mockAccessToken
        )
    }

    @Test
    fun `getSubscriptionPosts should emit Success state initially`() = runTest {
        val mockFeedPosts = listOf(mockFeedPost)
        coEvery { mockFeedPostSource.loadSubscriptionsFeed(any()) } returns mockFeedPosts

        repository.getSubscriptionPosts.test {
            assertEquals(ResultState.Success(emptyList<FeedPost>()), awaitItem())
            assertEquals(ResultState.Success(mockFeedPosts), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
        coVerify { mockFeedPostSource.loadSubscriptionsFeed(any()) }
    }

    @Test
    fun `getSubscriptionPosts should emit Empty state when sourceIds are empty`() = runTest {
        subscriptionsState.value = Subscription()

        repository.getSubscriptionPosts.test {
            assertEquals(ResultState.Success(emptyList<FeedPost>()), awaitItem())
            assertEquals(ResultState.Empty, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `getSubscriptionPosts should emit Error state when an exception occurs`() = runTest {
        val throwable = IOException("Network error")
        coEvery { mockFeedPostSource.loadSubscriptionsFeed(any()) } throws throwable

        repository.getSubscriptionPosts.test(timeout = 13.seconds) {
            assertEquals(ResultState.Success(emptyList<FeedPost>()), awaitItem())
            assertEquals(ResultState.Error(ErrorType.NETWORK_ERROR), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
        coVerify { mockFeedPostSource.loadSubscriptionsFeed(any()) }
    }

    @Test
    fun `loadNextData should trigger new data load`() = runTest {
        val mockFeedPosts = listOf(mockFeedPost)
        coEvery { mockFeedPostSource.loadSubscriptionsFeed(any()) } returns mockFeedPosts


        repository.loadNextData()
        advanceUntilIdle()


        repository.getSubscriptionPosts.test {
            assertEquals(ResultState.Success(emptyList<FeedPost>()), awaitItem())
            assertEquals(ResultState.Success(mockFeedPosts), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }

        coVerify { mockFeedPostSource.loadSubscriptionsFeed(any()) }
    }

    @Test
    fun `deletePost should remove post and emit updated list`() = runTest {
        val mockFeedPosts = listOf(mockFeedPost)
        coEvery { mockFeedPostSource.loadSubscriptionsFeed(any()) } returns mockFeedPosts
        coEvery { mockApiService.ignoreFeedPost(any(), 123L, 1L) } returns Unit

        repository.getSubscriptionPosts.test {
            assertEquals(ResultState.Success(emptyList<FeedPost>()), awaitItem())
            assertEquals(ResultState.Success(mockFeedPosts), awaitItem())
            repository.deletePost(mockFeedPost)
            advanceUntilIdle()
            assertEquals(ResultState.Success(emptyList<FeedPost>()), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
        coVerify { mockApiService.ignoreFeedPost("mockToken", 123L, 1L) }
    }

    @Test
    fun `changeLikeStatus should update post and emit updated list`() = runTest {
        val updatedFeedPost = mockFeedPost.copy(isLiked = true)
        val mockFeedPosts = listOf(mockFeedPost)
        coEvery { mockFeedPostSource.loadSubscriptionsFeed(any()) } returns mockFeedPosts
        coEvery { mockLikesSource.changeLikeStatus(mockFeedPost) } returns updatedFeedPost

        repository.getSubscriptionPosts.test {
            assertEquals(ResultState.Success(emptyList<FeedPost>()), awaitItem())
            assertEquals(ResultState.Success(mockFeedPosts), awaitItem())
            repository.changeLikeStatus(mockFeedPost)
            advanceUntilIdle()
            assertEquals(ResultState.Success(listOf(updatedFeedPost)), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `changeSubscriptionStatus should call subscriptionsSource`() = runTest {
        val updatedFeedPost = mockFeedPost.copy(isSubscribed = true)
        val mockFeedPosts = listOf(mockFeedPost)
        coEvery { mockFeedPostSource.loadSubscriptionsFeed(any()) } returns mockFeedPosts
        coEvery { mockSubscriptionsSource.changeSubscriptionStatus(mockFeedPost) } returns Unit


        repository.changeSubscriptionStatus(mockFeedPost)
        advanceUntilIdle()


        repository.getSubscriptionPosts.test {
            assertEquals(ResultState.Success(emptyList<FeedPost>()), awaitItem())
            assertEquals(ResultState.Success(listOf(updatedFeedPost)), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }


        coVerify { mockSubscriptionsSource.changeSubscriptionStatus(mockFeedPost) }
    }
}