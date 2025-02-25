package com.grebnev.vknewsclient.data.repository

import com.grebnev.vknewsclient.data.mapper.NewsFeedMapper
import com.grebnev.vknewsclient.data.network.ApiService
import com.grebnev.vknewsclient.data.source.AccessTokenSource
import com.grebnev.vknewsclient.data.source.LikesStatusSource
import com.grebnev.vknewsclient.data.source.SubscriptionsStatusSource
import com.grebnev.vknewsclient.di.scopes.ApplicationScope
import com.grebnev.vknewsclient.domain.entity.FeedPost
import com.grebnev.vknewsclient.domain.repository.RecommendationsFeedRepository
import com.grebnev.vknewsclient.extensions.mergeWith
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.retry
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@ApplicationScope
class RecommendationsFeedRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val mapper: NewsFeedMapper,
    private val likesStatus: LikesStatusSource,
    private val subscriptionsStatus: SubscriptionsStatusSource,
    private val accessToken: AccessTokenSource
) : RecommendationsFeedRepository {

    private val coroutineScope = CoroutineScope(Dispatchers.Default)

    private val nextDataNeededEvents = MutableSharedFlow<Unit>(replay = 1)
    private val refreshedListFlow = MutableSharedFlow<List<FeedPost>>()

    private val subscriptionsState = subscriptionsStatus.getSubscriptionsState()

    @OptIn(ExperimentalCoroutinesApi::class)
    private val loadedListFlow =
        subscriptionsStatus.loadSubscribes().flatMapConcat { subscription ->
            flow {
                nextDataNeededEvents.emit(Unit)
                nextDataNeededEvents.collect {
                    val startFrom = nextFrom

                    if (startFrom == null && feedPosts.isNotEmpty()) {
                        emit(feedPosts)
                        return@collect
                    }

                    val response = if (startFrom == null) {
                        apiService.loadRecommendations(accessToken.getAccessToken())
                    } else {
                        apiService.loadRecommendations(accessToken.getAccessToken(), startFrom)
                    }

                    nextFrom = response.newsFeedContent.nextFrom

                    val posts = mapper.mapResponseToFeedPost(response)
                    _feedPosts.addAll(posts)

                    _feedPosts.replaceAll { post ->
                        if (subscription.sourceIds.contains(post.communityId)) {
                            post.copy(isSubscribed = true)
                        } else {
                            post.copy(isSubscribed = false)
                        }
                    }

                    emit(feedPosts)
                }
            }.retry {
                delay(RETRY_TIMEOUT)
                true
            }
        }

    init {
        coroutineScope.launch {
            updateSubscriptionsStatus()
        }
    }

    private val _feedPosts = mutableListOf<FeedPost>()
    private val feedPosts: List<FeedPost>
        get() = _feedPosts.toList()

    private var nextFrom: String? = null

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
            refreshedListFlow.emit(feedPosts)
        }
    }

    private val recommendations: StateFlow<List<FeedPost>> = loadedListFlow
        .mergeWith(refreshedListFlow)
        .stateIn(
            scope = coroutineScope,
            started = SharingStarted.Lazily,
            initialValue = feedPosts
        )
    override val getRecommendations: StateFlow<List<FeedPost>> = recommendations

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
        refreshedListFlow.emit(feedPosts)
    }

    override suspend fun changeLikeStatus(feedPost: FeedPost) {
        val newPost = likesStatus.changeLikeStatus(feedPost)
        val postIndex = _feedPosts.indexOf(feedPost)
        _feedPosts[postIndex] = newPost
        refreshedListFlow.emit(feedPosts)
    }

    override suspend fun changeSubscriptionStatus(feedPost: FeedPost) {
        subscriptionsStatus.changeSubscriptionStatus(feedPost)
    }

    companion object {
        private const val RETRY_TIMEOUT = 3000L
    }
}