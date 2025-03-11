package com.grebnev.vknewsclient.data.repository

import com.grebnev.vknewsclient.core.handlers.ErrorHandler
import com.grebnev.vknewsclient.core.wrappers.ErrorType
import com.grebnev.vknewsclient.core.wrappers.ResultState
import com.grebnev.vknewsclient.data.mapper.NewsFeedMapper
import com.grebnev.vknewsclient.data.network.ApiService
import com.grebnev.vknewsclient.data.source.AccessTokenSource
import com.grebnev.vknewsclient.domain.entity.FeedPost
import com.grebnev.vknewsclient.domain.entity.PostComment
import com.grebnev.vknewsclient.domain.repository.CommentsPostRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
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

class CommentsPostRepositoryImpl
    @Inject
    constructor(
        private val apiService: ApiService,
        private val mapper: NewsFeedMapper,
        private val accessToken: AccessTokenSource,
    ) : CommentsPostRepository {
        private val coroutineScope = CoroutineScope(Dispatchers.Default)

        private val retryTrigger = MutableSharedFlow<Unit>(replay = 1)

        init {
            coroutineScope.launch {
                retryTrigger.emit(Unit)
            }
        }

        override fun getComments(feedPost: FeedPost): StateFlow<ResultState<List<PostComment>, ErrorType>> =
            loadComments(feedPost)
                .stateIn(
                    scope = coroutineScope,
                    started = SharingStarted.Lazily,
                    initialValue = ResultState.Initial,
                )

        @OptIn(ExperimentalCoroutinesApi::class)
        private fun loadComments(feedPost: FeedPost): Flow<ResultState<List<PostComment>, ErrorType>> =
            retryTrigger.flatMapLatest {
                flow {
                    val response =
                        apiService.loadComments(
                            token = accessToken.getAccessToken(),
                            ownerId = feedPost.communityId,
                            postId = feedPost.id,
                        )
                    val postComments = mapper.mapResponseToPostComment(response)
                    if (postComments.isNotEmpty()) {
                        emit(ResultState.Success(postComments) as ResultState<List<PostComment>, ErrorType>)
                    } else {
                        emit(ResultState.Empty)
                    }
                }.retry(ErrorHandler.MAX_COUNT_RETRY) {
                    delay(ErrorHandler.RETRY_TIMEOUT)
                    true
                }.catch { throwable ->
                    Timber.e(throwable)
                    val errorType = ErrorHandler.getErrorType(throwable)
                    emit(ResultState.Error(errorType))
                }
            }

        override suspend fun retry() {
            retryTrigger.emit(Unit)
        }
    }