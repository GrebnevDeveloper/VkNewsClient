package com.grebnev.vknewsclient.data.repository

import com.grebnev.vknewsclient.core.extensions.mergeWith
import com.grebnev.vknewsclient.core.handlers.ErrorHandler
import com.grebnev.vknewsclient.core.wrappers.ErrorType
import com.grebnev.vknewsclient.core.wrappers.ResultState
import com.grebnev.vknewsclient.data.network.ApiService
import com.grebnev.vknewsclient.data.source.AccessTokenSource
import com.grebnev.vknewsclient.data.source.FeedPostSource
import com.grebnev.vknewsclient.data.source.LikesStatusSource
import com.grebnev.vknewsclient.data.source.SubscriptionsStatusSource
import com.grebnev.vknewsclient.di.scopes.ApplicationScope
import com.grebnev.vknewsclient.domain.entity.FeedPost
import com.grebnev.vknewsclient.domain.repository.RecommendationsFeedRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.retryWhen
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@ApplicationScope
class RecommendationsFeedRepositoryImpl
    @Inject
    constructor(
        private val apiService: ApiService,
        private val feedPostSource: FeedPostSource,
        private val likesSource: LikesStatusSource,
        private val subscriptionsSource: SubscriptionsStatusSource,
        private val accessToken: AccessTokenSource,
    ) : RecommendationsFeedRepository {
        private val coroutineScope = CoroutineScope(Dispatchers.Default)

        private val nextDataNeededEvents = MutableSharedFlow<Unit>(replay = 1)
        private val nextFromState = feedPostSource.nextFromState
        private val subscriptionsState = subscriptionsSource.getSubscriptionsState()

        private val _feedPosts = mutableListOf<FeedPost>()
        private val feedPosts: List<FeedPost>
            get() = _feedPosts.toList()

        private val refreshedListFlow = MutableSharedFlow<ResultState<List<FeedPost>, ErrorType>>()

        private val recommendationsFeedFlow: Flow<ResultState<List<FeedPost>, ErrorType>> =
            flow {
                nextDataNeededEvents.emit(Unit)
                nextDataNeededEvents.collect {
                    val startFrom = nextFromState.value

                    if (startFrom == null && feedPosts.isNotEmpty()) {
                        emit(ResultState.Success(feedPosts) as ResultState<List<FeedPost>, ErrorType>)
                        return@collect
                    }

                    val posts = feedPostSource.loadRecommendationsFeed()
                    _feedPosts.addAll(posts)

                    val sourceIds = subscriptionsState.value.sourceIds
                    _feedPosts.replaceAll { post ->
                        if (sourceIds.contains(post.communityId)) {
                            post.copy(isSubscribed = true)
                        } else {
                            post.copy(isSubscribed = false)
                        }
                    }

                    emit(ResultState.Success(feedPosts))
                }
            }.retryWhen { cause, attempt ->
                if (attempt <= ErrorHandler.MAX_COUNT_RETRY) {
                    delay(ErrorHandler.RETRY_TIMEOUT)
                } else {
                    val errorType = ErrorHandler.getErrorType(cause)
                    emit(ResultState.Error(errorType))
                    delay(ErrorHandler.RETRY_TIMEOUT * 2)
                }
                true
            }

        init {
            coroutineScope.launch {
                updateSubscriptionsStatus()
            }
        }

        private suspend fun updateSubscriptionsStatus() {
            subscriptionsState.collect { subscription ->
                val sourceIds = subscription.sourceIds
                _feedPosts.forEachIndexed { index, post ->
                    if (sourceIds.contains(post.communityId)) {
                        _feedPosts[index] = post.copy(isSubscribed = true)
                    } else {
                        _feedPosts[index] = post.copy(isSubscribed = false)
                    }
                }
                refreshedListFlow.emit(ResultState.Success(feedPosts))
            }
        }

        private val recommendations: StateFlow<ResultState<List<FeedPost>, ErrorType>> =
            recommendationsFeedFlow
                .mergeWith(refreshedListFlow)
                .stateIn(
                    scope = coroutineScope,
                    started = SharingStarted.Lazily,
                    initialValue = ResultState.Success(feedPosts),
                )
        override val getRecommendations: StateFlow<ResultState<List<FeedPost>, ErrorType>> = recommendations

        override suspend fun loadNextData() {
            nextDataNeededEvents.emit(Unit)
        }

        override suspend fun deletePost(feedPost: FeedPost) {
            apiService.ignoreFeedPost(
                token = accessToken.getAccessToken(),
                ownerId = feedPost.communityId,
                postId = feedPost.id,
            )
            _feedPosts.remove(feedPost)
            refreshedListFlow.emit(ResultState.Success(feedPosts))
        }

        override suspend fun changeLikeStatus(feedPost: FeedPost) {
            val newPost = likesSource.changeLikeStatus(feedPost)
            val postIndex = _feedPosts.indexOf(feedPost)
            _feedPosts[postIndex] = newPost
            refreshedListFlow.emit(ResultState.Success(feedPosts))
        }

        override suspend fun changeSubscriptionStatus(feedPost: FeedPost) {
            subscriptionsSource.changeSubscriptionStatus(feedPost)
        }
    }