package com.grebnev.vknewsclient.domain.usecases

import com.grebnev.vknewsclient.domain.repository.NewsFeedRepository
import javax.inject.Inject

class GetRecommendationsUseCase @Inject constructor(
    private val repository: NewsFeedRepository
) {
    operator fun invoke() = repository.getRecommendations
}