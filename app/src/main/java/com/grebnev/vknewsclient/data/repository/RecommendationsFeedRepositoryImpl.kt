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
                    val newPosts = mutableListOf<FeedPost>()

                    for (post in posts) {
                        if (subscription.sourceIds.contains(post.communityId)) {
                            newPosts.add(post.copy(isSubscribed = true))
                        } else {
                            newPosts.add(post)
                        }
                    }

                    _feedPosts.addAll(newPosts)
                    emit(feedPosts)
                }
            }.retry {
                delay(RETRY_TIMEOUT)
                true
            }
        }

    private val _feedPosts = mutableListOf<FeedPost>()
    private val feedPosts: List<FeedPost>
        get() = _feedPosts.toList()

    private var nextFrom: String? = null

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
        val newPost = subscriptionsStatus.changeSubscriptionStatus(feedPost)
        val postIndex = _feedPosts.indexOf(feedPost)
        _feedPosts[postIndex] = newPost
        refreshedListFlow.emit(feedPosts)
    }

    companion object {
        private const val RETRY_TIMEOUT = 3000L
    }
}