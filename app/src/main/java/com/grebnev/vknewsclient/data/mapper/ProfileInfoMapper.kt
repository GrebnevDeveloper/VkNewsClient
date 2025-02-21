package com.grebnev.vknewsclient.data.mapper

import com.grebnev.vknewsclient.data.model.profile.ProfileInfoResponseDto
import com.grebnev.vknewsclient.domain.entity.ProfileInfo
import javax.inject.Inject

class ProfileInfoMapper @Inject constructor() {
    fun mapResponseToProfileInfo(response: ProfileInfoResponseDto): ProfileInfo {
        val profileInfoDto = response.profileInfo
        return ProfileInfo(
            id = profileInfoDto.id,
            avatarUrl = profileInfoDto.avatarUrl,
            firstName = profileInfoDto.firstName,
            lastName = profileInfoDto.lastName
        )
    }
}