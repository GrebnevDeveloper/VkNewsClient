package com.grebnev.vknewsclient.presentation.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import com.grebnev.vknewsclient.ui.theme.VkNewsClientTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            VkNewsClientTheme {
                val viewModel: MainViewModel = viewModel()
                val authState = viewModel.authState.collectAsState()

                when (authState.value) {
                    is AuthState.Authorized -> {
                        VkNewsMainScreen()
                    }

                    is AuthState.NotAuthorized -> {
                        VkIdAuthScreen(
                            onSuccessAuth = { oAuth, accessToken ->
                                viewModel.onSuccess()
                            },
                            onFailureAuth = { oAuth, fail ->
                                viewModel.onFail()
                            }
                        )
                    }

                    else -> {
                        viewModel.onFail()
                    }
                }
            }
        }
    }
}
