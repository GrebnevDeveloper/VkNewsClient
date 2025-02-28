package com.grebnev.vknewsclient.domain.usecases

import com.grebnev.vknewsclient.domain.repository.ProfileInfoRepository
import com.grebnev.vknewsclient.domain.state.ProfileInfoState
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class GetProfileInfoUseCase @Inject constructor(
    private val repository: ProfileInfoRepository
) {
    val getProfileInfo: StateFlow<ProfileInfoState> = repository.getProfileInfo

    suspend fun retry() {
        repository.retry()
    }
}