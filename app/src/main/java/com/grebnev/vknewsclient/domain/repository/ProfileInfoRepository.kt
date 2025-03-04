package com.grebnev.vknewsclient.domain.repository

import com.grebnev.vknewsclient.core.wrappers.ResultState
import com.grebnev.vknewsclient.domain.entity.ProfileInfo
import com.grebnev.vknewsclient.core.wrappers.ErrorType
import kotlinx.coroutines.flow.StateFlow

interface ProfileInfoRepository {
    val getProfileInfo: StateFlow<ResultState<ProfileInfo, ErrorType>>
    suspend fun retry()
}