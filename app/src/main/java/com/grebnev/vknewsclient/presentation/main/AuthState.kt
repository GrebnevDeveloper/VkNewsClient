package com.grebnev.vknewsclient.presentation.main

import com.vk.id.AccessToken
import com.vk.id.VKIDAuthFail

sealed class AuthState {
    data object Initial : AuthState()

    data class Authorized(
        val accessToken: AccessToken
    ) : AuthState()

    data class NotAuthorized(
        val fail: VKIDAuthFail
    ) : AuthState()

}