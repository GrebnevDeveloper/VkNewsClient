package com.grebnev.vknewsclient.domain.usecases

import com.grebnev.vknewsclient.core.wrappers.ResultState
import com.grebnev.vknewsclient.domain.entity.ProfileInfo
import com.grebnev.vknewsclient.domain.repository.ProfileInfoRepository
import com.grebnev.vknewsclient.core.wrappers.ErrorType
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class GetProfileInfoUseCase @Inject constructor(
    private val repository: ProfileInfoRepository
) {
    val getProfileInfo: StateFlow<ResultState<ProfileInfo, ErrorType>> = repository.getProfileInfo

    suspend fun retry() {
        repository.retry()
    }
}