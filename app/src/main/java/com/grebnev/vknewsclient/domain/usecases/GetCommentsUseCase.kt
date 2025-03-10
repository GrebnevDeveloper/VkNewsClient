package com.grebnev.vknewsclient.domain.usecases

import com.grebnev.vknewsclient.domain.entity.FeedPost
import com.grebnev.vknewsclient.domain.repository.CommentsPostRepository
import javax.inject.Inject

class GetCommentsUseCase
    @Inject
    constructor(
        private val repository: CommentsPostRepository,
    ) {
        fun getCommentsPost(feedPost: FeedPost) = repository.getComments(feedPost)

        suspend fun retry() = repository.retry()
    }