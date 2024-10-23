package com.grebnev.vknewsclient

import com.grebnev.vknewsclient.domain.FeedPost
import com.grebnev.vknewsclient.domain.PostComment

sealed class CommentsScreenState {

    data object Initial : CommentsScreenState()

    data class Comments(
        val feedPost: FeedPost,
        val comments: List<PostComment>
    ) : CommentsScreenState()
}