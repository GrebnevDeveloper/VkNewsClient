package com.grebnev.vknewsclient.domain.repository

import com.grebnev.vknewsclient.core.wrappers.ResultState
import com.grebnev.vknewsclient.domain.entity.FeedPost
import com.grebnev.vknewsclient.core.wrappers.ErrorType
import kotlinx.coroutines.flow.StateFlow

interface RecommendationsFeedRepository : NewsFeedRepository {
    val getRecommendations: StateFlow<ResultState<List<FeedPost>, ErrorType>>
}