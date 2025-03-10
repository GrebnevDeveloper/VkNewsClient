package com.grebnev.vknewsclient.domain.usecases

import com.grebnev.vknewsclient.di.keys.NewsFeedType
import com.grebnev.vknewsclient.domain.entity.FeedPost
import com.grebnev.vknewsclient.domain.repository.NewsFeedRepository
import javax.inject.Inject

class ChangeLikeStatusUseCase
    @Inject
    constructor(
        private val repositories: Map<NewsFeedType, @JvmSuppressWildcards NewsFeedRepository>,
    ) {
        suspend operator fun invoke(
            feedPost: FeedPost,
            type: NewsFeedType,
        ) {
            val repository =
                repositories[type] ?: throw IllegalArgumentException("Unknown repository type: $type")
            repository.changeLikeStatus(feedPost)
        }
    }