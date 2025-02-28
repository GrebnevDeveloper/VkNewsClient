package com.grebnev.vknewsclient.data.repository

import com.grebnev.vknewsclient.data.ErrorHandlingValues
import com.grebnev.vknewsclient.data.mapper.ProfileInfoMapper
import com.grebnev.vknewsclient.data.network.ApiService
import com.grebnev.vknewsclient.data.source.AccessTokenSource
import com.grebnev.vknewsclient.domain.repository.ProfileInfoRepository
import com.grebnev.vknewsclient.domain.state.ProfileInfoState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.retry
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class ProfileInfoRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val mapper: ProfileInfoMapper,
    private val accessToken: AccessTokenSource
) : ProfileInfoRepository {

    private val coroutineScope = CoroutineScope(Dispatchers.Default)

    private val retryTrigger = MutableSharedFlow<Unit>(replay = 1)

    init {
        coroutineScope.launch {
            retryTrigger.emit(Unit)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private val profileInfoFlow = retryTrigger.flatMapLatest {
        flow {
            val response = apiService.loadProfileInfo(accessToken.getAccessToken())
            val profileInfo = mapper.mapResponseToProfileInfo(response)
            emit(ProfileInfoState.Profile(profileInfo) as ProfileInfoState)
        }.retry(ErrorHandlingValues.MAX_COUNT_RETRY) {
            delay(ErrorHandlingValues.RETRY_TIMEOUT)
            true
        }.catch { throwable ->
            Timber.e(throwable.message)
            val typeError = ErrorHandlingValues.getTypeError(throwable)
            emit(ProfileInfoState.Error(typeError) as ProfileInfoState)
        }
    }

    override val getProfileInfo: StateFlow<ProfileInfoState> = profileInfoFlow
        .stateIn(
            scope = coroutineScope,
            started = SharingStarted.Lazily,
            initialValue = ProfileInfoState.Initial
        )

    override suspend fun retry() {
        retryTrigger.emit(Unit)
    }
}