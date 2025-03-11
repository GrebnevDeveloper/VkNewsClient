package com.grebnev.vknewsclient.presentation.news.recommendations

import com.grebnev.vknewsclient.domain.entity.FeedPost

sealed class RecommendationsFeedScreenState {
    data object Initial : RecommendationsFeedScreenState()

    data object Loading : RecommendationsFeedScreenState()

    data object NoRecommendations : RecommendationsFeedScreenState()

    data class Posts(
        val posts: List<FeedPost>,
        val nextDataLoading: Boolean = false,
    ) : RecommendationsFeedScreenState()

    data class Error(
        val message: String,
    ) : RecommendationsFeedScreenState()
}