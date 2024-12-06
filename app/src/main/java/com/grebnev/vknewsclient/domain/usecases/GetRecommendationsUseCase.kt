package com.grebnev.vknewsclient.domain.usecases

import com.grebnev.vknewsclient.domain.repository.NewsFeedRepository

class GetRecommendationsUseCase(
    private val repository: NewsFeedRepository
) {
    operator fun invoke() = repository.getRecommendations
}