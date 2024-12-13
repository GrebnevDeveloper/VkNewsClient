package com.grebnev.vknewsclient.presentation.profile

import com.grebnev.vknewsclient.domain.entity.ProfileInfo

sealed class ProfileInfoScreenState {

    data object Initial : ProfileInfoScreenState()

    data class Profile(
        val profileInfo: ProfileInfo
    ) : ProfileInfoScreenState()
}