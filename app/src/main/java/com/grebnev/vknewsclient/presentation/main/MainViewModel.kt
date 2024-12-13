package com.grebnev.vknewsclient.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vk.id.AccessToken
import com.vk.id.VKID
import com.vk.id.VKIDAuthFail
import com.vk.id.logout.VKIDLogoutCallback
import com.vk.id.logout.VKIDLogoutFail
import com.vk.id.refresh.VKIDRefreshTokenCallback
import com.vk.id.refresh.VKIDRefreshTokenFail
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainViewModel @Inject constructor() : ViewModel() {
    private val _authState = MutableStateFlow<AuthState>(AuthState.Initial)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    fun onSuccess(accessToken: AccessToken) {
        _authState.value = AuthState.Authorized(accessToken)
    }

    fun onFail(fail: VKIDAuthFail) {
        _authState.value = AuthState.NotAuthorized(fail)
    }

    init {
        val isAuth = VKID.instance.accessToken != null
        if (isAuth) {
            refreshToken()
        } else {
            _authState.value = AuthState.NotAuthorized(
                VKIDAuthFail.FailedOAuth("Access token is null")
            )
        }
    }

    private fun refreshToken() {
        viewModelScope.launch {
            VKID.instance.refreshToken(
                callback = object : VKIDRefreshTokenCallback {
                    override fun onSuccess(token: AccessToken) {
                        _authState.value = AuthState.Authorized(token)
                    }

                    override fun onFail(fail: VKIDRefreshTokenFail) {
                        _authState.value = AuthState.NotAuthorized(
                            VKIDAuthFail.FailedOAuth(fail.description)
                        )
                    }
                }
            )
        }
    }

    fun logout() {
        viewModelScope.launch {
            VKID.instance.logout(
                callback = object : VKIDLogoutCallback {
                    override fun onSuccess() {
                        _authState.value = AuthState.NotAuthorized(
                            VKIDAuthFail.FailedOAuth("The account was logged out. Access token is null")
                        )
                    }

                    override fun onFail(fail: VKIDLogoutFail) {
                        when (fail) {
                            is VKIDLogoutFail.FailedApiCall -> fail.description
                            is VKIDLogoutFail.NotAuthenticated -> fail.description
                            is VKIDLogoutFail.AccessTokenTokenExpired -> fail
                        }
                    }
                }
            )
        }
    }
}