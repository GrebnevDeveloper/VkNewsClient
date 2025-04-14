package com.grebnev.vknewsclient.presentation.main

import android.os.Bundle
import android.view.Window
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.grebnev.vknewsclient.R
import com.grebnev.vknewsclient.presentation.getApplicationComponent
import com.grebnev.vknewsclient.presentation.main.auth.AuthState
import com.grebnev.vknewsclient.presentation.main.auth.VkIdAuthScreen
import com.grebnev.vknewsclient.presentation.main.auth.VkNewsMainScreen
import com.grebnev.vknewsclient.ui.theme.VkNewsClientTheme
import timber.log.Timber

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_VkNewsClient)

        requestWindowFeature(Window.FEATURE_NO_TITLE)

        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.setBackgroundDrawableResource(android.R.color.transparent)

        enableEdgeToEdge()
        installSplashScreen()
        setContent {
            val component = getApplicationComponent()
            val viewModel: MainViewModel = viewModel(factory = component.getViewModelFactory())
            val authState = viewModel.authState.collectAsState()

            VkNewsClientTheme {
                when (authState.value) {
                    is AuthState.Authorized -> {
                        Timber.d(
                            "Auth success, token: ${
                                (authState.value as AuthState.Authorized)
                                    .accessToken
                                    .token
                            }",
                        )
                        VkNewsMainScreen(
                            onLogout = {
                                viewModel.logout()
                            },
                        )
                    }

                    is AuthState.NotAuthorized -> {
                        Timber.d(
                            "Auth failure, token: ${
                                (authState.value as AuthState.NotAuthorized)
                                    .fail
                                    .description
                            }",
                        )
                        VkIdAuthScreen(
                            onSuccessAuth = { accessToken ->
                                viewModel.onSuccess(accessToken)
                                Timber.d("Auth success, token: ${accessToken.token}")
                            },
                            onFailureAuth = { fail ->
                                viewModel.onFail(fail)
                                Timber.d("Auth failure, token: ${fail.description}")
                            },
                        )
                    }

                    else -> {
                    }
                }
            }
        }
    }
}