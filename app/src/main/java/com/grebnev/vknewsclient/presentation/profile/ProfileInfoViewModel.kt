package com.grebnev.vknewsclient.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grebnev.vknewsclient.core.extensions.mergeWith
import com.grebnev.vknewsclient.core.wrappers.ErrorType
import com.grebnev.vknewsclient.core.wrappers.ResultStatus
import com.grebnev.vknewsclient.domain.entity.ProfileInfo
import com.grebnev.vknewsclient.domain.usecases.GetProfileInfoUseCase
import com.grebnev.vknewsclient.presentation.base.ErrorMessageProvider
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class ProfileInfoViewModel
    @Inject
    constructor(
        private val profileInfoUseCase: GetProfileInfoUseCase,
        private val errorMessage: ErrorMessageProvider,
    ) : ViewModel() {
        private val loadNextDataFlow = MutableSharedFlow<ProfileInfoScreenState>()

        val screenState =
            profileInfoUseCase.getProfileInfo
                .map { mapResultStateToScreenState(it) }
                .onStart { emit(ProfileInfoScreenState.Loading) }
                .mergeWith(loadNextDataFlow)
                .catch { throwable ->
                    Timber.e(throwable)
                    ProfileInfoScreenState.Error(throwable.message ?: "Unknown error")
                }.stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.Lazily,
                    initialValue = ProfileInfoScreenState.Initial,
                )

        private fun mapResultStateToScreenState(
            profileInfoState: ResultStatus<ProfileInfo, ErrorType>,
        ): ProfileInfoScreenState =
            when (profileInfoState) {
                is ResultStatus.Success ->
                    ProfileInfoScreenState.Profile(profileInfoState.data)

                is ResultStatus.Error ->
                    ProfileInfoScreenState.Error(
                        errorMessage.getErrorMessage(profileInfoState.error),
                    )

                is ResultStatus.Empty ->
                    ProfileInfoScreenState.Initial
            }

        fun refreshedProfileInfo() {
            viewModelScope.launch {
                loadNextDataFlow.emit(ProfileInfoScreenState.Loading)
                profileInfoUseCase.retry()
            }
        }
    }