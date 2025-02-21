package com.grebnev.vknewsclient.domain.repository

import com.grebnev.vknewsclient.domain.entity.FeedPost
import com.grebnev.vknewsclient.domain.entity.PostComment
import kotlinx.coroutines.flow.StateFlow

interface CommentsPostRepository {
    fun getComments(feedPost: FeedPost): StateFlow<List<PostComment>>
}