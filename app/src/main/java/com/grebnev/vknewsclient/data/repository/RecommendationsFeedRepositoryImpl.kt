package com.grebnev.vknewsclient.data.repository

import com.grebnev.vknewsclient.data.ErrorHandlingValues
import com.grebnev.vknewsclient.data.network.ApiService
import com.grebnev.vknewsclient.data.source.AccessTokenSource
import com.grebnev.vknewsclient.data.source.FeedPostSource
import com.grebnev.vknewsclient.data.source.LikesStatusSource
import com.grebnev.vknewsclient.data.source.SubscriptionsStatusSource
import com.grebnev.vknewsclient.di.scopes.ApplicationScope
import com.grebnev.vknewsclient.domain.entity.FeedPost
import com.grebnev.vknewsclient.domain.repository.RecommendationsFeedRepository
import com.grebnev.vknewsclient.domain.state.FeedPostState
import com.grebnev.vknewsclient.extensions.mergeWith
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
class RecommendationsFeedRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val feedPostSource: FeedPostSource,
    private val likesSource: LikesStatusSource,
    private val subscriptionsSource: SubscriptionsStatusSource,
    private val accessToken: AccessTokenSource
) : RecommendationsFeedRepository {

    private val coroutineScope = CoroutineScope(Dispatchers.Default)

    private val nextDataNeededEvents = MutableSharedFlow<Unit>(replay = 1)
    private val nextFromState = feedPostSource.getNextFromState()
    private val subscriptionsState = subscriptionsSource.getSubscriptionsState()

    private val _feedPosts = mutableListOf<FeedPost>()
    private val feedPosts: List<FeedPost>
        get() = _feedPosts.toList()

    private val refreshedListFlow = MutableSharedFlow<FeedPostState>()

    private val recommendationsFeedFlow: Flow<FeedPostState> = flow {
        nextDataNeededEvents.emit(Unit)
        nextDataNeededEvents.collect {
            val startFrom = nextFromState.value

            if (startFrom == null && feedPosts.isNotEmpty()) {
                emit(FeedPostState.Posts(feedPosts) as FeedPostState)
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

            emit(FeedPostState.Posts(feedPosts) as FeedPostState)
        }
    }.retryWhen { cause, attempt ->
        if (attempt <= ErrorHandlingValues.MAX_COUNT_RETRY) {
            delay(ErrorHandlingValues.RETRY_TIMEOUT)
        } else {
            val type = ErrorHandlingValues.getTypeError(cause)
            emit(FeedPostState.Error(type))
            delay(ErrorHandlingValues.RETRY_TIMEOUT * 2)
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
            refreshedListFlow.emit(FeedPostState.Posts(feedPosts))
        }
    }

    private val recommendations: StateFlow<FeedPostState> = recommendationsFeedFlow
        .mergeWith(refreshedListFlow)
        .stateIn(
            scope = coroutineScope,
            started = SharingStarted.Lazily,
            initialValue = FeedPostState.Posts(feedPosts)
        )
    override val getRecommendations: StateFlow<FeedPostState> = recommendations

    override suspend fun loadNextData() {
        nextDataNeededEvents.emit(Unit)
    }

    override suspend fun deletePost(feedPost: FeedPost) {
        apiService.ignoreFeedPost(
            token = accessToken.getAccessToken(),
            ownerId = feedPost.communityId,
            postId = feedPost.id
        )
        _feedPosts.remove(feedPost)
        refreshedListFlow.emit(FeedPostState.Posts(feedPosts))
    }

    override suspend fun changeLikeStatus(feedPost: FeedPost) {
        val newPost = likesSource.changeLikeStatus(feedPost)
        val postIndex = _feedPosts.indexOf(feedPost)
        _feedPosts[postIndex] = newPost
        refreshedListFlow.emit(FeedPostState.Posts(feedPosts))
    }

    override suspend fun changeSubscriptionStatus(feedPost: FeedPost) {
        subscriptionsSource.changeSubscriptionStatus(feedPost)
    }
}