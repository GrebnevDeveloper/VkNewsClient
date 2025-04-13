package com.grebnev.vknewsclient.domain.usecases

import com.grebnev.vknewsclient.core.wrappers.ErrorType
import com.grebnev.vknewsclient.core.wrappers.ResultStatus
import com.grebnev.vknewsclient.domain.entity.ProfileInfo
import com.grebnev.vknewsclient.domain.repository.ProfileInfoRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetProfileInfoUseCase
    @Inject
    constructor(
        private val repository: ProfileInfoRepository,
    ) {
        val getProfileInfo: Flow<ResultStatus<ProfileInfo, ErrorType>> = repository.getProfileInfo

        suspend fun retry() = repository.retry()

        fun close() = repository.close()
    }