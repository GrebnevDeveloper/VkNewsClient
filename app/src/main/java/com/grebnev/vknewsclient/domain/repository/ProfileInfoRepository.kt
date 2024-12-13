package com.grebnev.vknewsclient.domain.repository

import com.grebnev.vknewsclient.domain.entity.ProfileInfo
import kotlinx.coroutines.flow.StateFlow

interface ProfileInfoRepository {
    fun getProfileInfo(): StateFlow<ProfileInfo>
}