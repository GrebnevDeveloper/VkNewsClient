package com.grebnev.vknewsclient.presentation.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import com.grebnev.vknewsclient.presentation.getApplicationComponent
import com.grebnev.vknewsclient.ui.theme.VkNewsClientTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val component = getApplicationComponent()
            val viewModel: MainViewModel = viewModel(factory = component.getViewModelFactory())
            val authState = viewModel.authState.collectAsState()

            VkNewsClientTheme {
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
