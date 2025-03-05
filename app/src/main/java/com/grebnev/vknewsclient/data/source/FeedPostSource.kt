package com.grebnev.vknewsclient.data.source

import androidx.annotation.VisibleForTesting
import com.grebnev.vknewsclient.data.mapper.NewsFeedMapper
import com.grebnev.vknewsclient.data.network.ApiService
import com.grebnev.vknewsclient.domain.entity.FeedPost
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

import javax.inject.Inject

class FeedPostSource @Inject constructor(
    private val apiService: ApiService,
    private val accessToken: AccessTokenSource,
    private val mapper: NewsFeedMapper
) {
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    internal val _nextFromState = MutableStateFlow<String?>(null)
    val nextFromState: StateFlow<String?> = _nextFromState

    suspend fun loadRecommendationsFeed(): List<FeedPost> {
        val startFrom = nextFromState.value
        val response = if (startFrom == null) {
            apiService.loadRecommendations(accessToken.getAccessToken())
        } else {
            apiService.loadRecommendations(accessToken.getAccessToken(), startFrom)
        }

        _nextFromState.value = response.newsFeedContent.nextFrom

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

        _nextFromState.value = response.newsFeedContent.nextFrom

        return mapper.mapResponseToFeedPost(response)
    }
}