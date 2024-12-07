package com.grebnev.vknewsclient.presentation.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import com.grebnev.vknewsclient.presentation.App
import com.grebnev.vknewsclient.presentation.ViewModelFactory
import com.grebnev.vknewsclient.ui.theme.VkNewsClientTheme
import javax.inject.Inject

class MainActivity : ComponentActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val component by lazy {
        (application as App).component
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        component.inject(this)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            VkNewsClientTheme {
                val viewModel: MainViewModel = viewModel(factory = viewModelFactory)
                val authState = viewModel.authState.collectAsState()

                when (authState.value) {
                    is AuthState.Authorized -> {
                        VkNewsMainScreen(viewModelFactory)
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
