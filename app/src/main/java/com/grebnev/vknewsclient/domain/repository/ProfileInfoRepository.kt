package com.grebnev.vknewsclient.domain.repository

import com.grebnev.vknewsclient.domain.state.ProfileInfoState
import kotlinx.coroutines.flow.StateFlow

interface ProfileInfoRepository {
    val getProfileInfo: StateFlow<ProfileInfoState>
    suspend fun retry()
}