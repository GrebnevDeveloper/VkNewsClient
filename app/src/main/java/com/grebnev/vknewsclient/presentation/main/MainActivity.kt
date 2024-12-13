package com.grebnev.vknewsclient.presentation.main

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import com.grebnev.vknewsclient.presentation.getApplicationComponent
import com.grebnev.vknewsclient.ui.theme.VkNewsClientTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        installSplashScreen()
        setContent {
            val component = getApplicationComponent()
            val viewModel: MainViewModel = viewModel(factory = component.getViewModelFactory())
            val authState = viewModel.authState.collectAsState()

            VkNewsClientTheme {
                when (authState.value) {
                    is AuthState.Authorized -> {
                        Log.d(
                            "AuthState",
                            "Auth success, token: ${
                                (authState.value as AuthState.Authorized)
                                    .accessToken
                                    .token
                            }"
                        )
                        VkNewsMainScreen(
                            onLogout = {
                                viewModel.logout()
                            }
                        )
                    }

                    is AuthState.NotAuthorized -> {
                        Log.d(
                            "AuthState",
                            "Auth failure, token: ${
                                (authState.value as AuthState.NotAuthorized)
                                    .fail
                                    .description
                            }"
                        )
                        VkIdAuthScreen(
                            onSuccessAuth = { accessToken ->
                                viewModel.onSuccess(accessToken)
                                Log.d("AuthState", "Auth success, token: ${accessToken.token}")
                            },
                            onFailureAuth = { fail ->
                                viewModel.onFail(fail)
                                Log.d("AuthState", "Auth failure, token: ${fail.description}")
                            }
                        )
                    }

                    else -> {
                    }
                }
            }
        }
    }
}
