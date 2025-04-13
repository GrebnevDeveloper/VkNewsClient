package com.grebnev.vknewsclient.domain.repository

import com.grebnev.vknewsclient.core.wrappers.ErrorType
import com.grebnev.vknewsclient.core.wrappers.ResultStatus
import com.grebnev.vknewsclient.domain.entity.ProfileInfo
import kotlinx.coroutines.flow.Flow

interface ProfileInfoRepository {
    val getProfileInfo: Flow<ResultStatus<ProfileInfo, ErrorType>>

    suspend fun retry()

    fun close()
}