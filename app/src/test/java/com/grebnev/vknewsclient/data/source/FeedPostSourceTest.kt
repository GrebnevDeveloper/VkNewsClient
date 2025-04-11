package com.grebnev.vknewsclient.data.source

import app.cash.turbine.test
import com.grebnev.vknewsclient.data.mapper.NewsFeedMapper
import com.grebnev.vknewsclient.data.model.news.posts.NewsFeedResponseDto
import com.grebnev.vknewsclient.data.network.ApiService
import com.grebnev.vknewsclient.domain.entity.FeedPost
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class FeedPostSourceTest {
    private lateinit var feedPostSource: FeedPostSource
    private lateinit var mockApiService: ApiService
    private lateinit var mockAccessToken: AccessTokenSource
    private lateinit var mockMapper: NewsFeedMapper

    @Before
    fun setUp() {
        mockApiService = mockk()
        mockAccessToken =
            mockk {
                coEvery { getAccessToken() } returns "mockToken"
            }
        mockMapper = mockk()
        feedPostSource = FeedPostSource(mockApiService, mockAccessToken, mockMapper)
    }

    @Test
    fun `loadRecommendationsFeed should return mapped posts and update nextFromState`() =
        runTest {
            val mockResponse =
                mockk<NewsFeedResponseDto> {
                    every { newsFeedContent.nextFrom } returns "nextFromToken"
                    every { newsFeedContent.posts } returns listOf(mockk())
                    every { newsFeedContent.groups } returns listOf(mockk())
                }
            val mockFeedPosts = listOf(mockk<FeedPost>())

            coEvery { mockApiService.loadRecommendations(any()) } returns mockResponse
            coEvery { mockMapper.mapResponseToFeedPost(mockResponse) } returns mockFeedPosts

            feedPostSource.hasNextFromState.test {
                assertEquals(null, feedPostSource.nextFrom)
                assertFalse(awaitItem())

                val result = feedPostSource.loadRecommendationsFeed()

                assertEquals("nextFromToken", feedPostSource.nextFrom)
                assertTrue(awaitItem())
                assertEquals(mockFeedPosts, result)
            }
            advanceUntilIdle()

            coVerify { mockApiService.loadRecommendations("mockToken") }
            coVerify { mockMapper.mapResponseToFeedPost(mockResponse) }
        }

    @Test
    fun `loadRecommendationsFeed with startFrom should call API with nextFrom`() =
        runTest {
            val mockResponse =
                mockk<NewsFeedResponseDto> {
                    every { newsFeedContent.nextFrom } returns "nextFromToken"
                    every { newsFeedContent.posts } returns listOf(mockk())
                    every { newsFeedContent.groups } returns listOf(mockk())
                }
            val mockFeedPosts = listOf(mockk<FeedPost>())

            coEvery { mockApiService.loadRecommendations(any(), "startFromToken") } returns mockResponse
            coEvery { mockMapper.mapResponseToFeedPost(mockResponse) } returns mockFeedPosts

            feedPostSource.nextFrom = "startFromToken"

            feedPostSource.hasNextFromState.test {
                assertEquals("startFromToken", feedPostSource.nextFrom)
                assertFalse(awaitItem())

                val result = feedPostSource.loadRecommendationsFeed()

                assertEquals("nextFromToken", feedPostSource.nextFrom)
                assertTrue(awaitItem())

                assertEquals(mockFeedPosts, result)
            }
            advanceUntilIdle()

            coVerify { mockApiService.loadRecommendations(any(), "startFromToken") }
            coVerify { mockMapper.mapResponseToFeedPost(mockResponse) }
        }

    @Test
    fun `loadSubscriptionsFeed should return mapped posts and update nextFromState`() =
        runTest {
            val mockResponse =
                mockk<NewsFeedResponseDto> {
                    every { newsFeedContent.nextFrom } returns "nextFromToken"
                    every { newsFeedContent.posts } returns listOf(mockk())
                    every { newsFeedContent.groups } returns listOf(mockk())
                }
            val mockFeedPosts = listOf(mockk<FeedPost>())

            coEvery {
                mockApiService.loadSubscriptionPosts(
                    token = "mockToken",
                    sourceIds = "123,456",
                )
            } returns mockResponse
            coEvery { mockMapper.mapResponseToFeedPost(mockResponse) } returns mockFeedPosts

            feedPostSource.hasNextFromState.test {
                assertEquals(null, feedPostSource.nextFrom)
                assertFalse(awaitItem())

                val result = feedPostSource.loadSubscriptionsFeed("123,456")

                assertEquals("nextFromToken", feedPostSource.nextFrom)
                assertTrue(awaitItem())
                assertEquals(mockFeedPosts, result)
            }
            advanceUntilIdle()

            coVerify {
                mockApiService.loadSubscriptionPosts(
                    token = "mockToken",
                    sourceIds = "123,456",
                )
            }
        }

    @Test
    fun `loadSubscriptionsFeed with startFrom should call API with nextFrom`() =
        runTest {
            val mockResponse =
                mockk<NewsFeedResponseDto> {
                    every { newsFeedContent.nextFrom } returns "nextFromToken"
                    every { newsFeedContent.posts } returns listOf(mockk())
                    every { newsFeedContent.groups } returns listOf(mockk())
                }
            val mockFeedPosts = listOf(mockk<FeedPost>())

            coEvery {
                mockApiService.loadSubscriptionPosts(
                    token = "mockToken",
                    sourceIds = "123,456",
                    nextFrom = "startFromToken",
                )
            } returns mockResponse
            coEvery { mockMapper.mapResponseToFeedPost(mockResponse) } returns mockFeedPosts

            feedPostSource.nextFrom = "startFromToken"

            feedPostSource.hasNextFromState.test {
                assertEquals("startFromToken", feedPostSource.nextFrom)
                assertFalse(awaitItem())

                val result = feedPostSource.loadSubscriptionsFeed("123,456")

                assertEquals("nextFromToken", feedPostSource.nextFrom)
                assertTrue(awaitItem())

                assertEquals(mockFeedPosts, result)
            }
            advanceUntilIdle()

            coVerify {
                mockApiService.loadSubscriptionPosts(
                    token = "mockToken",
                    sourceIds = "123,456",
                    nextFrom = "startFromToken",
                )
            }
            coVerify { mockMapper.mapResponseToFeedPost(mockResponse) }
        }
}