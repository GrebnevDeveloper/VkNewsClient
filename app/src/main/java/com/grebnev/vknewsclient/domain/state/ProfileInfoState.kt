package com.grebnev.vknewsclient.domain.state

import com.grebnev.vknewsclient.domain.entity.ProfileInfo

sealed class ProfileInfoState {
    data object Initial : ProfileInfoState()

    data class Profile(
        val profile: ProfileInfo
    ) : ProfileInfoState()

    data class Error(
        val type: ErrorType
    ) : ProfileInfoState()
}