package com.grebnev.vknewsclient.domain.usecases

import com.grebnev.vknewsclient.domain.repository.ProfileInfoRepository
import javax.inject.Inject

class GetProfileInfoUseCase @Inject constructor(
    private val repository: ProfileInfoRepository
) {
    operator fun invoke() = repository.getProfileInfo()
}