package com.grebnev.vknewsclient.domain.usecases

import com.grebnev.vknewsclient.domain.entity.FeedPost
import com.grebnev.vknewsclient.domain.repository.CommentsPostRepository
import com.grebnev.vknewsclient.domain.repository.NewsFeedRepository
import javax.inject.Inject

class GetCommentsUseCase @Inject constructor (
    private val repository: CommentsPostRepository
) {
    operator fun invoke(feedPost: FeedPost) = repository.getComments(feedPost)
}