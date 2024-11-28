package com.grebnev.vknewsclient.presentation.main

sealed class AuthState {
    data object Authorized: AuthState()
    data object NotAuthorized : AuthState()

    data object Initial : AuthState()
}