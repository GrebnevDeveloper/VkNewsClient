package com.grebnev.vknewsclient.domain.state

import com.grebnev.vknewsclient.domain.entity.FeedPost

sealed class FeedPostState {
    data object NoPosts : FeedPostState()

    data class Posts(
        val posts: List<FeedPost>
    ) : FeedPostState()

    data class Error(
        val type: ErrorType
    ) : FeedPostState()
}