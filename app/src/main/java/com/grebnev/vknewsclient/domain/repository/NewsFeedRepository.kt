package com.grebnev.vknewsclient.domain.repository

import com.grebnev.vknewsclient.domain.entity.FeedPost
import kotlinx.coroutines.flow.StateFlow

interface NewsFeedRepository {
    suspend fun loadNextData()

    suspend fun deletePost(feedPost: FeedPost)

    suspend fun changeLikeStatus(feedPost: FeedPost)

    suspend fun changeSubscriptionStatus(feedPost: FeedPost)

    fun hasNextDataLoading(): StateFlow<Boolean>

    fun close()
}