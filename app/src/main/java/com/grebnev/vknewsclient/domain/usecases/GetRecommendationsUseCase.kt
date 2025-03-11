package com.grebnev.vknewsclient.domain.usecases

import com.grebnev.vknewsclient.domain.repository.RecommendationsFeedRepository
import javax.inject.Inject

class GetRecommendationsUseCase
    @Inject
    constructor(
        private val repository: RecommendationsFeedRepository,
    ) {
        operator fun invoke() = repository.getRecommendations
    }