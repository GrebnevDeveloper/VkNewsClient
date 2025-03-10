package com.grebnev.vknewsclient.data.source

import app.cash.turbine.test
import com.grebnev.vknewsclient.data.mapper.NewsFeedMapper
import com.grebnev.vknewsclient.data.model.subscriptions.SubscriptionsIdDto
import com.grebnev.vknewsclient.data.network.ApiService
import com.grebnev.vknewsclient.domain.entity.FeedPost
import com.grebnev.vknewsclient.domain.entity.Subscription
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class SubscriptionsStatusSourceTest {
    private lateinit var mockApiService: ApiService
    private lateinit var mockMapper: NewsFeedMapper
    private lateinit var mockAccessToken: AccessTokenSource

    private lateinit var subscriptionsStatusSource: SubscriptionsStatusSource

    @Before
    fun setUp() {
        mockApiService = mockk()
        mockMapper = mockk()
        mockAccessToken =
            mockk {
                coEvery { getAccessToken() } returns "mockToken"
            }

        subscriptionsStatusSource =
            SubscriptionsStatusSource(
                mockApiService,
                mockMapper,
                mockAccessToken,
            )
    }

    @Test
    fun `getSubscriptionsState should emit initial state`() =
        runTest {
            val mockSubscription =
                Subscription(
                    id = 1L,
                    title = "SubscriptionsVkNews",
                    sourceIds = setOf(-123, 456),
                )
            coEvery { mockApiService.getListSubscriptions(any()) } returns mockk()
            coEvery { mockMapper.mapResponseToSubscriptions(any()) } returns mockSubscription

            subscriptionsStatusSource.getSubscriptionsState().test {
                assertEquals(Subscription(), awaitItem())
                assertEquals(mockSubscription, awaitItem())
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `getSubscriptionsState should emit updated state after changeSubscriptionStatus`() =
        runTest {
            val mockSubscription =
                Subscription(
                    id = 1L,
                    title = "SubscriptionsVkNews",
                    sourceIds = setOf(-123, 456),
                )
            val updatedSubscription = mockSubscription.copy(sourceIds = setOf(-123, 456, 789))
            val mockFeedPost =
                FeedPost(
                    id = 1L,
                    communityId = 789L,
                    communityName = "Community 1",
                    publicationDate = "2023-10-01",
                    communityImageUrl = "https://example.com/community.jpg",
                    contentText = "Post content",
                    contentImageUrl = "https://example.com/post.jpg",
                    statisticsList = emptyList(),
                    isLiked = false,
                    isSubscribed = false,
                )

            coEvery { mockApiService.getListSubscriptions(any()) } returns mockk()
            coEvery {
                mockMapper.mapResponseToSubscriptions(any())
            } returns mockSubscription andThen updatedSubscription
            coEvery {
                mockApiService.saveListSubscriptions(
                    any(),
                    mockSubscription.id,
                    mockSubscription.title,
                    "-123, 456, 789",
                )
            } returns SubscriptionsIdDto(mockSubscription.id)

            subscriptionsStatusSource.getSubscriptionsState().test {
                assertEquals(Subscription(), awaitItem())
                assertEquals(mockSubscription, awaitItem())
                subscriptionsStatusSource.changeSubscriptionStatus(mockFeedPost)
                advanceUntilIdle()
                assertEquals(updatedSubscription, awaitItem())
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `changeSubscriptionStatus should remove subscription if sourceIds are empty`() =
        runTest {
            val mockSubscription =
                Subscription(
                    id = 1L,
                    title = "SubscriptionsVkNews",
                    sourceIds = setOf(789),
                )
            val mockFeedPost =
                FeedPost(
                    id = 1L,
                    communityId = 789L,
                    communityName = "Community 1",
                    publicationDate = "2023-10-01",
                    communityImageUrl = "https://example.com/community.jpg",
                    contentText = "Post content",
                    contentImageUrl = "https://example.com/post.jpg",
                    statisticsList = emptyList(),
                    isLiked = false,
                    isSubscribed = true,
                )

            coEvery { mockApiService.getListSubscriptions(any()) } returns mockk()
            coEvery {
                mockMapper.mapResponseToSubscriptions(any())
            } returns mockSubscription andThen Subscription()
            coEvery {
                mockApiService.deleteListSubscriptions(any(), mockSubscription.id)
            } returns Unit

            subscriptionsStatusSource.getSubscriptionsState().test {
                assertEquals(Subscription(), awaitItem())
                assertEquals(mockSubscription, awaitItem())
                subscriptionsStatusSource.changeSubscriptionStatus(mockFeedPost)
                advanceUntilIdle()
                assertEquals(Subscription(), awaitItem())
                cancelAndIgnoreRemainingEvents()
            }
        }
}