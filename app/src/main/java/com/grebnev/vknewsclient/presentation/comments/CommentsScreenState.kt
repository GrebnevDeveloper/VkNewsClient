package com.grebnev.vknewsclient.presentation.comments

import com.grebnev.vknewsclient.domain.entity.FeedPost
import com.grebnev.vknewsclient.domain.entity.PostComment

sealed class CommentsScreenState {

    data object Initial : CommentsScreenState()
    data object Loading : CommentsScreenState()
    data object NoComments : CommentsScreenState()

    data class Comments(
        val feedPost: FeedPost,
        val comments: List<PostComment>
    ) : CommentsScreenState()

    data class Error(
        val message: String
    ) : CommentsScreenState()
}