package com.grebnev.vknewsclient.domain.repository

import com.grebnev.vknewsclient.core.wrappers.ErrorType
import com.grebnev.vknewsclient.core.wrappers.ResultStatus
import com.grebnev.vknewsclient.domain.entity.FeedPost
import com.grebnev.vknewsclient.domain.entity.PostComment
import kotlinx.coroutines.flow.Flow

interface CommentsPostRepository {
    fun getComments(feedPost: FeedPost): Flow<ResultStatus<List<PostComment>, ErrorType>>

    suspend fun retry()

    fun close()
}