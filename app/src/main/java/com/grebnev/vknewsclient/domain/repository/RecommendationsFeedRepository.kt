package com.grebnev.vknewsclient.domain.repository

import com.grebnev.vknewsclient.domain.entity.FeedPost
import kotlinx.coroutines.flow.StateFlow

interface RecommendationsFeedRepository : NewsFeedRepository {
    val getRecommendations: StateFlow<List<FeedPost>>
}