package com.grebnev.vknewsclient.presentation.profile

import androidx.lifecycle.ViewModel
import com.grebnev.vknewsclient.domain.usecases.GetProfileInfoUseCase
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ProfileInfoViewModel @Inject constructor(
    private val getProfileInfoUseCase: GetProfileInfoUseCase
) : ViewModel() {

    val screenState = getProfileInfoUseCase()
        .map {
            ProfileInfoScreenState.Profile(it)
        }
}