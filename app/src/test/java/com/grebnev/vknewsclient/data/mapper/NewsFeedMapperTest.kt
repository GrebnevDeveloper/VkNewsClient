package com.grebnev.vknewsclient.data.mapper

import com.grebnev.vknewsclient.data.model.comments.CommentDto
import com.grebnev.vknewsclient.data.model.comments.CommentsResponseDto
import com.grebnev.vknewsclient.data.model.news.posts.GroupDto
import com.grebnev.vknewsclient.data.model.news.posts.NewsFeedResponseDto
import com.grebnev.vknewsclient.data.model.news.posts.PostDto
import com.grebnev.vknewsclient.data.model.profile.ProfileDto
import com.grebnev.vknewsclient.data.model.subscriptions.SubscriptionsDto
import com.grebnev.vknewsclient.data.model.subscriptions.SubscriptionsResponseDto
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NewsFeedMapperTest {
    private lateinit var newsFeedMapper: NewsFeedMapper

    @Before
    fun setUp() {
        newsFeedMapper = NewsFeedMapper()
    }

    @Test
    fun `mapResponseToFeedPost should map NewsFeedResponseDto to FeedPost with correct data`() {
        val post =
            mockk<PostDto> {
                every { id } returns 1L
                every { communityId } returns 123L
                every { date } returns 1672531200L
                every { text } returns "Test post"
                every { attachments } returns
                    listOf(
                        mockk {
                            every { photo } returns
                                mockk {
                                    every { photoUrls } returns
                                        listOf(
                                            mockk {
                                                every { photoUrl } returns
                                                    "https://example.com/image.jpg"
                                            },
                                        )
                                }
                        },
                    )
                every { likes } returns
                    mockk {
                        every { count } returns 10
                        every { userLikes } returns 1
                    }
                every { views } returns
                    mockk {
                        every { count } returns 100
                    }
                every { comments } returns
                    mockk {
                        every { count } returns 5
                    }
                every { reposts } returns
                    mockk {
                        every { count } returns 2
                    }
            }
        val group =
            mockk<GroupDto> {
                every { id } returns 123L
                every { name } returns "Test Group"
                every { imageUrl } returns "https://example.com/group.jpg"
            }
        val response =
            mockk<NewsFeedResponseDto> {
                every { newsFeedContent } returns
                    mockk {
                        every { posts } returns listOf(post)
                        every { groups } returns listOf(group)
                    }
            }
        val expectedDate =
            SimpleDateFormat("d MMMM yyyy, hh:mm", Locale.getDefault()).format(Date(1672531200L * 1000))

        val result = newsFeedMapper.mapResponseToFeedPost(response)
        val feedPost = result[0]

        assertEquals(1, result.size)
        assertEquals(1L, feedPost.id)
        assertEquals(123L, feedPost.communityId)
        assertEquals("Test Group", feedPost.communityName)
        assertEquals(expectedDate, feedPost.publicationDate)
        assertEquals("https://example.com/group.jpg", feedPost.communityImageUrl)
        assertEquals("Test post", feedPost.contentText)
        assertEquals("https://example.com/image.jpg", feedPost.contentImageUrl)
        assertEquals(4, feedPost.statisticsList.size)
        assertTrue(feedPost.isLiked)
        assertFalse(feedPost.isSubscribed)
    }

    @Test
    fun `mapResponseToPostComment should map CommentsResponseDto to PostComment with correct data`() {
        val comment =
            mockk<CommentDto> {
                every { id } returns 1L
                every { authorId } returns 123L
                every { date } returns 1672531200L
                every { text } returns "Test comment"
            }
        val profile =
            mockk<ProfileDto> {
                every { id } returns 123L
                every { firstName } returns "John"
                every { lastName } returns "Doe"
                every { authorAvatarUrl } returns "https://example.com/avatar.jpg"
            }
        val response =
            mockk<CommentsResponseDto> {
                every { commentsContent } returns
                    mockk {
                        every { comments } returns listOf(comment)
                        every { profiles } returns listOf(profile)
                    }
            }
        val expectedDate =
            SimpleDateFormat("d MMMM yyyy, hh:mm", Locale.getDefault()).format(Date(1672531200L * 1000))

        val result = newsFeedMapper.mapResponseToPostComment(response)
        val postComment = result[0]

        assertEquals(1, result.size)
        assertEquals(1L, postComment.id)
        assertEquals("https://example.com/avatar.jpg", postComment.authorAvatarUrl)
        assertEquals("John Doe", postComment.authorName)
        assertEquals("Test comment", postComment.commentText)
        assertEquals(expectedDate, postComment.publicationDate)
    }

    @Test
    fun `mapResponseToSubscriptions map SubscriptionsResponseDto to Subscriptions with correct data`() {
        val subscription =
            mockk<SubscriptionsDto> {
                every { id } returns 1L
                every { title } returns "SubscriptionsVkNews"
                every { sourceIds } returns setOf(-123L, 456L)
            }
        val response =
            mockk<SubscriptionsResponseDto> {
                every { listSubscriptionContent } returns
                    mockk {
                        every { listSubscriptions } returns listOf(subscription)
                    }
            }

        val result = newsFeedMapper.mapResponseToSubscriptions(response)

        assertEquals(1L, result.id)
        assertEquals("SubscriptionsVkNews", result.title)
        assertEquals(setOf(-123L, 456L), result.sourceIds)
    }

    @Test
    fun `mapResponseToSubscriptions should use initial data when mapping SubscriptionsResponseDto`() {
        val subscription =
            mockk<SubscriptionsDto> {
                every { id } returns 1L
                every { title } returns "OtherTitle"
                every { sourceIds } returns setOf(123L, -456L)
            }
        val response =
            mockk<SubscriptionsResponseDto> {
                every { listSubscriptionContent } returns
                    mockk {
                        every { listSubscriptions } returns listOf(subscription)
                    }
            }

        val result = newsFeedMapper.mapResponseToSubscriptions(response)

        assertEquals(1001L, result.id)
        assertEquals("SubscriptionsVkNews", result.title)
        assertTrue(result.sourceIds.isEmpty())
    }
}