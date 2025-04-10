package com.grebnev.vknewsclient.domain.repository

import com.grebnev.vknewsclient.core.wrappers.ErrorType
import com.grebnev.vknewsclient.core.wrappers.ResultStatus
import com.grebnev.vknewsclient.domain.entity.FeedPost
import kotlinx.coroutines.flow.StateFlow

interface SubscriptionsFeedRepository : NewsFeedRepository {
    val getSubscriptionPosts: StateFlow<ResultStatus<List<FeedPost>, ErrorType>>
}