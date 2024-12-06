package com.grebnev.vknewsclient.presentation.news

import com.grebnev.vknewsclient.domain.entity.FeedPost

sealed class NewsFeedScreenState {
    data object Initial : NewsFeedScreenState()

    data object Loading : NewsFeedScreenState()

    data class Posts(
        val posts: List<FeedPost>,
        val nextDataLoading: Boolean = false
    ) : NewsFeedScreenState()
}