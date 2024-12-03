package com.grebnev.vknewsclient.data.repository

import com.grebnev.vknewsclient.data.mapper.NewsFeedMapper
import com.grebnev.vknewsclient.data.network.ApiFactory
import com.grebnev.vknewsclient.domain.FeedPost
import com.grebnev.vknewsclient.domain.StatisticItem
import com.grebnev.vknewsclient.domain.StatisticType
import com.vk.id.VKID

class NewsFeedRepository {

    private val apiService = ApiFactory.apiService
    private val mapper = NewsFeedMapper()

    private var nextFrom: String? = null

    private val _feedPosts = mutableListOf<FeedPost>()
    val feedPosts: List<FeedPost>
        get() = _feedPosts.toList()

    private fun getAccessToken(): String {
        val token = VKID.instance.accessToken?.token ?: throw IllegalStateException("Token is null")
        return token
    }

    suspend fun loadRecommendations(): List<FeedPost> {
        val startFrom = nextFrom

        if (startFrom == null && feedPosts.isNotEmpty()) return feedPosts

        val response = if (startFrom == null) {
            apiService.loadRecommendations(getAccessToken())
        } else {
            apiService.loadRecommendations(getAccessToken(), startFrom)
        }

        nextFrom = response.newsFeedContent.nextFrom

        val posts = mapper.mapResponseToFeedPost(response)
        _feedPosts.addAll(posts)
        return feedPosts
    }

    suspend fun changeLikeStatus(feedPost: FeedPost) {
        val response = if (!feedPost.isLiked) {
            apiService.addLike(
                token = getAccessToken(),
                ownerId = feedPost.communityId,
                postId = feedPost.id
            )
        } else {
            apiService.deleteLike(
                token = getAccessToken(),
                ownerId = feedPost.communityId,
                postId = feedPost.id
            )
        }
        val likesCount = response.likes.count
        val newStatistics = feedPost.statisticsList.toMutableList().apply {
            removeIf { it.type == StatisticType.LIKES }
            add(StatisticItem(StatisticType.LIKES, likesCount))
        }
        val newPost = feedPost.copy(statisticsList = newStatistics, isLiked = !feedPost.isLiked)
        val postIndex = _feedPosts.indexOf(feedPost)
        _feedPosts[postIndex] = newPost
    }
}