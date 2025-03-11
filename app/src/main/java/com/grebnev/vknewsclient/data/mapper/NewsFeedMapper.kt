package com.grebnev.vknewsclient.data.mapper

import com.grebnev.vknewsclient.data.model.comments.CommentsResponseDto
import com.grebnev.vknewsclient.data.model.news.posts.NewsFeedResponseDto
import com.grebnev.vknewsclient.data.model.subscriptions.SubscriptionsResponseDto
import com.grebnev.vknewsclient.domain.entity.FeedPost
import com.grebnev.vknewsclient.domain.entity.PostComment
import com.grebnev.vknewsclient.domain.entity.StatisticItem
import com.grebnev.vknewsclient.domain.entity.StatisticType
import com.grebnev.vknewsclient.domain.entity.Subscription
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import kotlin.math.absoluteValue

class NewsFeedMapper
    @Inject
    constructor() {
        fun mapResponseToFeedPost(response: NewsFeedResponseDto): List<FeedPost> {
            val result = mutableListOf<FeedPost>()

            val posts = response.newsFeedContent.posts
            val groups = response.newsFeedContent.groups

            for (post in posts) {
                val group = groups.find { it.id == post.communityId.absoluteValue } ?: break
                val feedPost =
                    FeedPost(
                        id = post.id,
                        communityId = post.communityId,
                        communityName = group.name,
                        publicationDate = mapTimestampToDate(post.date),
                        communityImageUrl = group.imageUrl,
                        contentText = post.text,
                        contentImageUrl =
                            post.attachments
                                ?.firstOrNull()
                                ?.photo
                                ?.photoUrls
                                ?.lastOrNull()
                                ?.photoUrl,
                        statisticsList =
                            listOf(
                                StatisticItem(StatisticType.LIKES, post.likes?.count ?: continue),
                                StatisticItem(StatisticType.VIEWS, post.views?.count ?: continue),
                                StatisticItem(StatisticType.COMMENTS, post.comments?.count ?: continue),
                                StatisticItem(StatisticType.SHARES, post.reposts?.count ?: continue),
                            ),
                        isLiked = post.likes.userLikes > 0,
                        isSubscribed = false,
                    )
                result.add(feedPost)
            }

            return result
        }

        fun mapResponseToPostComment(response: CommentsResponseDto): List<PostComment> {
            val result = mutableListOf<PostComment>()

            val comments = response.commentsContent.comments
            val profiles = response.commentsContent.profiles

            for (comment in comments) {
                if (comment.text.isBlank()) continue
                val author = profiles.firstOrNull { it.id == comment.authorId } ?: continue
                val postComment =
                    PostComment(
                        id = comment.id,
                        authorAvatarUrl = author.authorAvatarUrl,
                        authorName = "${author.firstName} ${author.lastName}",
                        commentText = comment.text,
                        publicationDate = mapTimestampToDate(comment.date),
                    )
                result.add(postComment)
            }

            return result
        }

        fun mapResponseToSubscriptions(response: SubscriptionsResponseDto): Subscription {
            val subscriptions = response.listSubscriptionContent.listSubscriptions

            for (subscription in subscriptions) {
                if (subscription.title == Subscription.UNIQUE_TITLE) {
                    return Subscription(
                        id = subscription.id,
                        title = subscription.title,
                        sourceIds = subscription.sourceIds,
                    )
                }
            }
            return Subscription()
        }

        private fun mapTimestampToDate(timestamp: Long): String {
            val date = Date(timestamp * 1000)
            return SimpleDateFormat("d MMMM yyyy, hh:mm", Locale.getDefault()).format(date)
        }
    }