package com.grebnev.vknewsclient.domain.repository

import com.grebnev.vknewsclient.domain.entity.FeedPost

interface NewsFeedRepository {
    suspend fun loadNextData()

    suspend fun deletePost(feedPost: FeedPost)

    suspend fun changeLikeStatus(feedPost: FeedPost)

    suspend fun changeSubscriptionStatus(feedPost: FeedPost)

    fun close()
}