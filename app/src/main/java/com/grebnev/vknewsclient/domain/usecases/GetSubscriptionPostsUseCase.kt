package com.grebnev.vknewsclient.domain.usecases

import com.grebnev.vknewsclient.domain.repository.SubscriptionsFeedRepository
import javax.inject.Inject

class GetSubscriptionPostsUseCase
    @Inject
    constructor(
        private val repository: SubscriptionsFeedRepository,
    ) {
        operator fun invoke() = repository.getSubscriptionPosts
    }