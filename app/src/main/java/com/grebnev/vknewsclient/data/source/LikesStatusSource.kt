package com.grebnev.vknewsclient.data.source

import com.grebnev.vknewsclient.data.network.ApiService
import com.grebnev.vknewsclient.domain.entity.FeedPost
import com.grebnev.vknewsclient.domain.entity.StatisticItem
import com.grebnev.vknewsclient.domain.entity.StatisticType
import javax.inject.Inject

class LikesStatusSource @Inject constructor(
    private val apiService: ApiService,
    private val accessToken: AccessTokenSource
) {
    suspend fun changeLikeStatus(feedPost: FeedPost): FeedPost {
        val response = if (!feedPost.isLiked) {
            apiService.addLike(
                token = accessToken.getAccessToken(),
                ownerId = feedPost.communityId,
                postId = feedPost.id
            )
        } else {
            apiService.deleteLike(
                token = accessToken.getAccessToken(),
                ownerId = feedPost.communityId,
                postId = feedPost.id
            )
        }
        val likesCount = response.likes.count
        val newStatistics = feedPost.statisticsList.toMutableList().apply {
            removeIf { it.type == StatisticType.LIKES }
            add(StatisticItem(StatisticType.LIKES, likesCount))
        }
        return feedPost.copy(statisticsList = newStatistics, isLiked = !feedPost.isLiked)
    }
}