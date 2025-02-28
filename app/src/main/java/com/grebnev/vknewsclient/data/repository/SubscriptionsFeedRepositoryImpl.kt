package com.grebnev.vknewsclient.data.repository

import com.grebnev.vknewsclient.data.ErrorHandlingValues
import com.grebnev.vknewsclient.data.network.ApiService
import com.grebnev.vknewsclient.data.source.AccessTokenSource
import com.grebnev.vknewsclient.data.source.FeedPostSource
import com.grebnev.vknewsclient.data.source.LikesStatusSource
import com.grebnev.vknewsclient.data.source.SubscriptionsStatusSource
import com.grebnev.vknewsclient.di.scopes.ApplicationScope
import com.grebnev.vknewsclient.domain.entity.FeedPost
import com.grebnev.vknewsclient.domain.repository.SubscriptionsFeedRepository
import com.grebnev.vknewsclient.domain.state.FeedPostState
import com.grebnev.vknewsclient.extensions.mergeWith
import kotlinx.coroutines.CoroutineExceptionHandler
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
import timber.log.Timber
import javax.inject.Inject

@ApplicationScope
class SubscriptionsFeedRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val feedPostSource: FeedPostSource,
    private val likesSource: LikesStatusSource,
    private val subscriptionsSource: SubscriptionsStatusSource,
    private val accessToken: AccessTokenSource
) : SubscriptionsFeedRepository {

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Timber.d("Coroutine exception handler was called ${throwable.message}")
    }

    private val coroutineScope = CoroutineScope(Dispatchers.Default + exceptionHandler)

    private val nextDataNeededEvents = MutableSharedFlow<Unit>(replay = 1)
    private val nextFromState = feedPostSource.getNextFromState()
    private val subscriptionsState = subscriptionsSource.getSubscriptionsState()

    private val _feedPosts = mutableListOf<FeedPost>()
    private val feedPosts: List<FeedPost>
        get() = _feedPosts.toList()

    private val refreshedListFlow = MutableSharedFlow<FeedPostState>()

    private val subscriptionsFeedFlow: Flow<FeedPostState> = flow {
        nextDataNeededEvents.emit(Unit)
        nextDataNeededEvents.collect {
            val sourceIds = subscriptionsState.value.sourceIds
            if (sourceIds.isEmpty()) {
                emit(FeedPostState.NoPosts as FeedPostState)
                return@collect
            }

            val startFrom = nextFromState.value

            if (startFrom == null && feedPosts.isNotEmpty()) {
                emit(FeedPostState.Posts(feedPosts) as FeedPostState)
                return@collect
            }

            val posts = feedPostSource.loadSubscriptionsFeed(sourceIds.joinToString())

            _feedPosts.addAll(posts.map { it.copy(isSubscribed = true) })

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
            if (_feedPosts.isEmpty() && sourceIds.isNotEmpty()) {
                nextDataNeededEvents.emit(Unit)
            }
            _feedPosts.forEachIndexed { index, post ->
                if (sourceIds.contains(post.communityId)) {
                    _feedPosts[index] = post.copy(isSubscribed = true)
                } else {
                    _feedPosts[index] = post.copy(isSubscribed = false)
                }
            }
            deletePostsAfterUnsubscribing()
            if (sourceIds.isEmpty()) {
                refreshedListFlow.emit(FeedPostState.NoPosts)
            } else {
                refreshedListFlow.emit(FeedPostState.Posts(feedPosts))
            }
        }
    }

    private val subscriptions: StateFlow<FeedPostState> = subscriptionsFeedFlow
        .mergeWith(refreshedListFlow)
        .stateIn(
            scope = coroutineScope,
            started = SharingStarted.Lazily,
            initialValue = FeedPostState.Posts(feedPosts)
        )

    override val getSubscriptionPosts: StateFlow<FeedPostState> = subscriptions

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

    private fun deletePostsAfterUnsubscribing() {
        val deletedPosts = _feedPosts.filter { !it.isSubscribed }
        _feedPosts.removeAll(deletedPosts)
    }
}