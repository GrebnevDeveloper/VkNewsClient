package com.grebnev.vknewsclient.data.source

import androidx.annotation.VisibleForTesting
import com.grebnev.vknewsclient.data.mapper.NewsFeedMapper
import com.grebnev.vknewsclient.data.network.ApiService
import com.grebnev.vknewsclient.domain.entity.FeedPost
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

class FeedPostSource
    @Inject
    constructor(
        private val apiService: ApiService,
        private val accessToken: AccessTokenSource,
        private val mapper: NewsFeedMapper,
    ) {
        @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
        internal val _hasNextFromState = MutableStateFlow<Boolean>(false)
        val hasNextFromState: StateFlow<Boolean> = _hasNextFromState.asStateFlow()

        @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
        internal var nextFrom: String? = null

        suspend fun loadRecommendationsFeed(): List<FeedPost> {
            val currentNextFrom = nextFrom
            val response =
                if (currentNextFrom == null) {
                    apiService.loadRecommendations(accessToken.getAccessToken())
                } else {
                    apiService.loadRecommendations(accessToken.getAccessToken(), currentNextFrom)
                }

            nextFrom = response.newsFeedContent.nextFrom
            _hasNextFromState.value = nextFrom != null

            return mapper.mapResponseToFeedPost(response)
        }

        suspend fun loadSubscriptionsFeed(sourceIds: String): List<FeedPost> {
            val currentNextFrom = nextFrom
            val response =
                if (currentNextFrom == null) {
                    apiService.loadSubscriptionPosts(
                        token = accessToken.getAccessToken(),
                        sourceIds = sourceIds,
                    )
                } else {
                    apiService.loadSubscriptionPosts(
                        token = accessToken.getAccessToken(),
                        sourceIds = sourceIds,
                        nextFrom = currentNextFrom,
                    )
                }

            nextFrom = response.newsFeedContent.nextFrom
            _hasNextFromState.value = nextFrom != null

            return mapper.mapResponseToFeedPost(response)
        }
    }