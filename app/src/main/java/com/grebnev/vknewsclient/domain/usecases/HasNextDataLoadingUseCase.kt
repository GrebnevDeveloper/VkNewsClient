package com.grebnev.vknewsclient.domain.usecases

import com.grebnev.vknewsclient.di.keys.NewsFeedType
import com.grebnev.vknewsclient.domain.repository.NewsFeedRepository
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class HasNextDataLoadingUseCase
    @Inject
    constructor(
        private val repositories: Map<NewsFeedType, @JvmSuppressWildcards NewsFeedRepository>,
    ) {
        operator fun invoke(type: NewsFeedType): StateFlow<Boolean> {
            val repository =
                repositories[type] ?: throw IllegalArgumentException("Unknown repository type: $type")
            return repository.hasNextDataLoading()
        }
    }