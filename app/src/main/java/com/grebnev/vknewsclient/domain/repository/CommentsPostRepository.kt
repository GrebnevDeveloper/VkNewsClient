package com.grebnev.vknewsclient.domain.repository

import com.grebnev.vknewsclient.domain.entity.FeedPost
import com.grebnev.vknewsclient.domain.state.PostCommentState
import kotlinx.coroutines.flow.StateFlow

interface CommentsPostRepository {
    fun getComments(feedPost: FeedPost): StateFlow<PostCommentState>
    suspend fun retry()
}