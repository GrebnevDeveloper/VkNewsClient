package com.grebnev.vknewsclient.domain.usecases

import com.grebnev.vknewsclient.domain.repository.NewsFeedRepository

class LoadNextDataUseCase(
    private val repository: NewsFeedRepository
) {
    suspend operator fun invoke() = repository.loadNextData()
}