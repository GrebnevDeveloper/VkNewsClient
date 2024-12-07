package com.grebnev.vknewsclient.domain.usecases

import com.grebnev.vknewsclient.domain.entity.FeedPost
import com.grebnev.vknewsclient.domain.repository.NewsFeedRepository
import javax.inject.Inject

class DeletePostUseCase @Inject constructor(
    private val repository: NewsFeedRepository
) {
    suspend operator fun invoke(feedPost: FeedPost) = repository.deletePost(feedPost)
}