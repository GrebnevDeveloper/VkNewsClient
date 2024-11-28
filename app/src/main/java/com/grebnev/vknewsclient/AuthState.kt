package com.grebnev.vknewsclient

sealed class AuthState {
    data object Authorized: AuthState()
    data object NotAuthorized : AuthState()

    data object Initial : AuthState()
}