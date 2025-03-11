package com.grebnev.vknewsclient.presentation.news.subscriptions

import com.grebnev.vknewsclient.domain.entity.FeedPost

sealed class SubscriptionsScreenState {
    data object Initial : SubscriptionsScreenState()

    data object Loading : SubscriptionsScreenState()

    data object NoSubscriptions : SubscriptionsScreenState()

    data class Posts(
        val posts: List<FeedPost>,
        val nextDataLoading: Boolean = false,
    ) : SubscriptionsScreenState()

    data class Error(
        val message: String,
    ) : SubscriptionsScreenState()
}