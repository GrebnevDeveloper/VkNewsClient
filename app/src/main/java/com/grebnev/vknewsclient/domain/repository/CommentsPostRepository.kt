package com.grebnev.vknewsclient.domain.repository

import com.grebnev.vknewsclient.core.wrappers.ErrorType
import com.grebnev.vknewsclient.core.wrappers.ResultState
import com.grebnev.vknewsclient.domain.entity.FeedPost
import com.grebnev.vknewsclient.domain.entity.PostComment
import kotlinx.coroutines.flow.StateFlow

interface CommentsPostRepository {
    fun getComments(feedPost: FeedPost): StateFlow<ResultState<List<PostComment>, ErrorType>>

    suspend fun retry()
}