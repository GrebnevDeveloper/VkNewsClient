package com.grebnev.vknewsclient.data.repository

import com.grebnev.vknewsclient.data.mapper.NewsFeedMapper
import com.grebnev.vknewsclient.data.network.ApiService
import com.grebnev.vknewsclient.data.source.AccessTokenSource
import com.grebnev.vknewsclient.domain.entity.FeedPost
import com.grebnev.vknewsclient.domain.entity.PostComment
import com.grebnev.vknewsclient.domain.repository.CommentsPostRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.retry
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

class CommentsPostRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val mapper: NewsFeedMapper,
    private val accessToken: AccessTokenSource
) : CommentsPostRepository {

    private val coroutineScope = CoroutineScope(Dispatchers.Default)

    override fun getComments(feedPost: FeedPost): StateFlow<List<PostComment>> =
        loadComments(feedPost)

    private fun loadComments(feedPost: FeedPost): StateFlow<List<PostComment>> = flow {
        val response = apiService.loadComments(
            token = accessToken.getAccessToken(),
            ownerId = feedPost.communityId,
            postId = feedPost.id
        )
        val postComments = mapper.mapResponseToPostComment(response)
        emit(postComments)
    }.retry {
        delay(RETRY_TIMEOUT)
        true
    }.stateIn(
        scope = coroutineScope,
        started = SharingStarted.Lazily,
        initialValue = listOf()
    )

    companion object {
        private const val RETRY_TIMEOUT = 3000L
    }
}