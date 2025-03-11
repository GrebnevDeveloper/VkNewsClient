package com.grebnev.vknewsclient.presentation.profile

import com.grebnev.vknewsclient.domain.entity.ProfileInfo

sealed class ProfileInfoScreenState {
    data object Initial : ProfileInfoScreenState()

    data object Loading : ProfileInfoScreenState()

    data class Profile(
        val profileInfo: ProfileInfo,
    ) : ProfileInfoScreenState()

    data class Error(
        val message: String,
    ) : ProfileInfoScreenState()
}