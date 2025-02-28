package com.grebnev.vknewsclient.domain.state

import com.grebnev.vknewsclient.domain.entity.PostComment

sealed class PostCommentState {
    data object Initial : PostCommentState()
    data object NoComments : PostCommentState()

    data class Comments(
        val comments: List<PostComment>
    ) : PostCommentState()

    data class Error(
        val type: ErrorType
    ) : PostCommentState()
}