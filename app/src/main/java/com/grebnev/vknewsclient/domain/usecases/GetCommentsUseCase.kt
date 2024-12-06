package com.grebnev.vknewsclient.domain.usecases

import com.grebnev.vknewsclient.domain.entity.FeedPost
import com.grebnev.vknewsclient.domain.repository.NewsFeedRepository

class GetCommentsUseCase(
    private val repository: NewsFeedRepository
) {
    operator fun invoke(feedPost: FeedPost) = repository.getComments(feedPost)
}