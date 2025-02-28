package com.grebnev.vknewsclient.data.source

import com.grebnev.vknewsclient.data.mapper.NewsFeedMapper
import com.grebnev.vknewsclient.data.network.ApiService
import com.grebnev.vknewsclient.domain.entity.FeedPost
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

class FeedPostSource @Inject constructor(
    private val apiService: ApiService,
    private val accessToken: AccessTokenSource,
    private val mapper: NewsFeedMapper
) {
    private val nextFromState = MutableStateFlow<String?>(null)

    fun getNextFromState() = nextFromState.asStateFlow()

    suspend fun loadRecommendationsFeed(): List<FeedPost> {
        val startFrom = nextFromState.value
        val response = if (startFrom == null) {
            apiService.loadRecommendations(accessToken.getAccessToken())
        } else {
            apiService.loadRecommendations(accessToken.getAccessToken(), startFrom)
        }

        nextFromState.value = response.newsFeedContent.nextFrom

        return mapper.mapResponseToFeedPost(response)
    }

    suspend fun loadSubscriptionsFeed(sourceIds: String): List<FeedPost> {
        val startFrom = nextFromState.value
        val response = if (startFrom == null) {
            apiService.loadSubscriptionPosts(
                token = accessToken.getAccessToken(),
                sourceIds = sourceIds
            )
        } else {
            apiService.loadSubscriptionPosts(
                token = accessToken.getAccessToken(),
                sourceIds = sourceIds,
                nextFrom = startFrom
            )
        }

        nextFromState.value = response.newsFeedContent.nextFrom

        return mapper.mapResponseToFeedPost(response)
    }
}