package com.grebnev.vknewsclient.presentation.news

import com.grebnev.vknewsclient.domain.FeedPost

sealed class NewsFeedScreenState {
    data object Initial : NewsFeedScreenState()

    data class Posts(val posts: List<FeedPost>) : NewsFeedScreenState()
}