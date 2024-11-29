package com.grebnev.vknewsclient.data.mapper

import com.grebnev.vknewsclient.data.model.NewsFeedResponseDto
import com.grebnev.vknewsclient.domain.FeedPost
import com.grebnev.vknewsclient.domain.StatisticItem
import com.grebnev.vknewsclient.domain.StatisticType
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
                communityName = group.name,
                publicationDate = post.date.toString(),
                communityImageUrl = group.imageUrl,
                contentText = post.text,
                contentImageUrl = post.attachments?.firstOrNull()?.photo?.photoUrls?.lastOrNull()?.photoUrl,
                statisticsList = listOf(
                    StatisticItem(StatisticType.LIKES, post.likes.count),
                    StatisticItem(StatisticType.VIEWS, post.views.count),
                    StatisticItem(StatisticType.COMMENTS, post.comments.count),
                    StatisticItem(StatisticType.SHARES, post.reposts.count),
                )
            )
            result.add(feedPost)
        }

        return result
    }
}