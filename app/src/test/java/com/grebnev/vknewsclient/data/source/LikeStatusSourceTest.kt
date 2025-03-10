package com.grebnev.vknewsclient.data.source

import com.grebnev.vknewsclient.data.model.news.statistics.LikesCountResponseDto
import com.grebnev.vknewsclient.data.network.ApiService
import com.grebnev.vknewsclient.domain.entity.FeedPost
import com.grebnev.vknewsclient.domain.entity.StatisticItem
import com.grebnev.vknewsclient.domain.entity.StatisticType
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class LikeStatusSourceTest {
    private lateinit var likesStatusSource: LikesStatusSource
    private lateinit var mockApiService: ApiService
    private lateinit var mockAccessToken: AccessTokenSource
    private lateinit var mockFeedPost: FeedPost

    @Before
    fun setUp() {
        mockApiService = mockk()
        mockAccessToken =
            mockk {
                coEvery { getAccessToken() } returns "mockToken"
            }

        mockFeedPost =
            mockk {
                every { isLiked } returns true
                every { communityId } returns 123
                every { id } returns 456
                every { statisticsList } returns
                    listOf(
                        StatisticItem(StatisticType.LIKES, 10),
                        StatisticItem(StatisticType.VIEWS, 100),
                    )
                every {
                    copy(
                        id = any(),
                        communityId = any(),
                        communityName = any(),
                        publicationDate = any(),
                        communityImageUrl = any(),
                        contentText = any(),
                        contentImageUrl = any(),
                        statisticsList = any(),
                        isLiked = any(),
                        isSubscribed = any(),
                    )
                } answers {
                    mockk {
                        every { statisticsList } returns arg(7)
                        every { isLiked } returns arg(8)
                    }
                }
            }
        likesStatusSource = LikesStatusSource(mockApiService, mockAccessToken)
    }

    @Test
    fun `changeLikeStatus should add like and update FeedPost when post is not liked`() =
        runTest {
            every { mockFeedPost.isLiked } returns false
            val mockResponse =
                mockk<LikesCountResponseDto> {
                    every { likes.count } returns 11
                }
            coEvery {
                mockApiService.addLike(
                    token = any(),
                    ownerId = 123,
                    postId = 456,
                )
            } returns mockResponse

            val updatedFeedPost = likesStatusSource.changeLikeStatus(mockFeedPost)

            assertEquals(11, updatedFeedPost.statisticsList.find { it.type == StatisticType.LIKES }?.count)
            assertTrue(updatedFeedPost.isLiked)

            coVerify {
                mockApiService.addLike(
                    token = "mockToken",
                    ownerId = 123,
                    postId = 456,
                )
            }
        }

    @Test
    fun `changeLikeStatus should remove like and update FeedPost when post is already liked`() =
        runTest {
            every { mockFeedPost.isLiked } returns true
            val mockResponse =
                mockk<LikesCountResponseDto> {
                    every { likes.count } returns 9
                }
            coEvery {
                mockApiService.deleteLike(
                    token = any(),
                    ownerId = 123,
                    postId = 456,
                )
            } returns mockResponse

            val updatedFeedPost = likesStatusSource.changeLikeStatus(mockFeedPost)

            assertEquals(9, updatedFeedPost.statisticsList.find { it.type == StatisticType.LIKES }?.count)
            assertFalse(updatedFeedPost.isLiked)

            coVerify {
                mockApiService.deleteLike(
                    token = "mockToken",
                    ownerId = 123,
                    postId = 456,
                )
            }
        }
}