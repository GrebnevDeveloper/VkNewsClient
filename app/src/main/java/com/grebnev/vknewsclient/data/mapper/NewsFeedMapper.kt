package com.grebnev.vknewsclient.data.mapper

import com.grebnev.vknewsclient.data.model.CommentsResponseDto
import com.grebnev.vknewsclient.data.model.NewsFeedResponseDto
import com.grebnev.vknewsclient.domain.entity.FeedPost
import com.grebnev.vknewsclient.domain.entity.PostComment
import com.grebnev.vknewsclient.domain.entity.StatisticItem
import com.grebnev.vknewsclient.domain.entity.StatisticType
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.absoluteValue

class NewsFeedMapper {
    fun mapResponseToFeedPost(response: NewsFeedResponseDto): List<FeedPost> {
        val result = mutableListOf<FeedPost>()

        val posts = response.newsFeedContent.posts
        val groups = response.newsFeedContent.groups

        for (post in posts) {
            val group = groups.find { it.id == post.communityId.absoluteValue } ?: break
            val feedPost = FeedPost(
                id = post.id,
                communityId = post.communityId,
                communityName = group.name,
                publicationDate = mapTimestampToDate(post.date),
                communityImageUrl = group.imageUrl,
                contentText = post.text,
                contentImageUrl = post.attachments?.firstOrNull()?.photo?.photoUrls?.lastOrNull()?.photoUrl,
                statisticsList = listOf(
                    StatisticItem(StatisticType.LIKES, post.likes.count),
                    StatisticItem(StatisticType.VIEWS, post.views.count),
                    StatisticItem(StatisticType.COMMENTS, post.comments.count),
                    StatisticItem(StatisticType.SHARES, post.reposts.count),
                ),
                isLiked = post.likes.userLikes > 0
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
            val postComment = PostComment(
                id = comment.id,
                authorAvatarUrl = author.authorAvatarUrl,
                authorName = "${author.firstName} ${author.lastName}",
                commentText = comment.text,
                publicationDate = mapTimestampToDate(comment.date)
            )
            result.add(postComment)
        }

        return result
    }

    private fun mapTimestampToDate(timestamp: Long): String {
        val date = Date(timestamp * 1000)
        return SimpleDateFormat("d MMMM yyyy, hh:mm", Locale.getDefault()).format(date)
    }
}