package com.grebnev.vknewsclient.data.repository

import com.grebnev.vknewsclient.core.handlers.ErrorHandler
import com.grebnev.vknewsclient.core.wrappers.ErrorType
import com.grebnev.vknewsclient.core.wrappers.ResultStatus
import com.grebnev.vknewsclient.data.mapper.NewsFeedMapper
import com.grebnev.vknewsclient.data.network.ApiService
import com.grebnev.vknewsclient.data.source.AccessTokenSource
import com.grebnev.vknewsclient.domain.entity.FeedPost
import com.grebnev.vknewsclient.domain.entity.PostComment
import com.grebnev.vknewsclient.domain.repository.CommentsPostRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.retry
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

        @OptIn(ExperimentalCoroutinesApi::class)
        override fun getComments(feedPost: FeedPost): Flow<ResultStatus<List<PostComment>, ErrorType>> =
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
                        emit(ResultStatus.Success(postComments) as ResultStatus<List<PostComment>, ErrorType>)
                    } else {
                        emit(ResultStatus.Empty)
                    }
                }.retry(ErrorHandler.MAX_COUNT_RETRY) {
                    delay(ErrorHandler.RETRY_TIMEOUT)
                    true
                }.catch { throwable ->
                    Timber.e(throwable)
                    val errorType = ErrorHandler.getErrorType(throwable)
                    emit(ResultStatus.Error(errorType))
                }
            }

        override suspend fun retry() {
            retryTrigger.emit(Unit)
        }

        override fun close() {
            coroutineScope.cancel()
        }
    }