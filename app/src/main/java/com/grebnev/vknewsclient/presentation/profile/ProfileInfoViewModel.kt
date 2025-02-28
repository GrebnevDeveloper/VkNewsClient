package com.grebnev.vknewsclient.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grebnev.vknewsclient.data.ErrorHandlingValues
import com.grebnev.vknewsclient.domain.state.ProfileInfoState
import com.grebnev.vknewsclient.domain.usecases.GetProfileInfoUseCase
import com.grebnev.vknewsclient.extensions.mergeWith
import com.grebnev.vknewsclient.presentation.ErrorMessageProvider
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class ProfileInfoViewModel @Inject constructor(
    private val profileInfoUseCase: GetProfileInfoUseCase,
    private val errorMessage: ErrorMessageProvider
) : ViewModel() {

    private val loadNextDataFlow = MutableSharedFlow<ProfileInfoScreenState>()

    val screenState = profileInfoUseCase.getProfileInfo
        .map { mapProfileInfoStateToScreenState(it) }
        .onStart { ProfileInfoScreenState.Loading }
        .mergeWith(loadNextDataFlow)
        .catch { throwable ->
            Timber.e(throwable.message)
            ProfileInfoState.Error(ErrorHandlingValues.getTypeError(throwable))
        }

    private fun mapProfileInfoStateToScreenState(
        profileInfoState: ProfileInfoState
    ): ProfileInfoScreenState {
        return when(profileInfoState) {
            is ProfileInfoState.Profile ->
                ProfileInfoScreenState.Profile(profileInfoState.profile)
            is ProfileInfoState.Error ->
                ProfileInfoScreenState.Error(
                    errorMessage.getErrorMessage(profileInfoState.type)
                )
            is ProfileInfoState.Initial ->
                ProfileInfoScreenState.Loading
        }
    }

    fun refreshedProfileInfo() {
        viewModelScope.launch {
            loadNextDataFlow.emit(ProfileInfoScreenState.Loading)
            profileInfoUseCase.retry()
        }
    }
}