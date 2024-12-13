package com.grebnev.vknewsclient.data.repository

import com.grebnev.vknewsclient.data.mapper.ProfileInfoMapper
import com.grebnev.vknewsclient.data.network.ApiService
import com.grebnev.vknewsclient.domain.entity.ProfileInfo
import com.grebnev.vknewsclient.domain.repository.ProfileInfoRepository
import com.vk.id.VKID
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

class ProfileInfoRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val mapper: ProfileInfoMapper
) : ProfileInfoRepository {

    private val coroutineScope = CoroutineScope(Dispatchers.Default)

    private val profileInfo = flow {
        val response = apiService.loadProfileInfo(getAccessToken())
        val profileInfo = mapper.mapResponseToProfileInfo(response)
        emit(profileInfo)
    }.stateIn(
        coroutineScope,
        SharingStarted.Lazily,
        ProfileInfo(0, "", "", "")
    )

    override fun getProfileInfo(): StateFlow<ProfileInfo> = profileInfo

    private fun getAccessToken(): String {
        val token = VKID.instance.accessToken?.token ?: throw IllegalStateException("Token is null")
        return token
    }
}