package com.grebnev.vknewsclient

import androidx.lifecycle.ViewModel
import com.vk.id.VKID
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MainViewModel : ViewModel() {
    private val _authState = MutableStateFlow<AuthState>(AuthState.Initial)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    fun onSuccess() {
        _authState.value = AuthState.Authorized
    }

    fun onFail() {
        _authState.value = AuthState.NotAuthorized
    }

    init {
        val isAuth = VKID.instance.accessToken != null
        if (isAuth) {
            _authState.value = AuthState.Authorized
        } else {
            _authState.value = AuthState.NotAuthorized
        }
    }
}