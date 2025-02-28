package com.grebnev.vknewsclient.domain.repository

import com.grebnev.vknewsclient.domain.state.FeedPostState
import kotlinx.coroutines.flow.StateFlow

interface SubscriptionsFeedRepository : NewsFeedRepository {
    val getSubscriptionPosts: StateFlow<FeedPostState>
}