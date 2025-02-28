package com.grebnev.vknewsclient.presentation.comments

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grebnev.vknewsclient.domain.entity.FeedPost
import com.grebnev.vknewsclient.domain.state.PostCommentState
import com.grebnev.vknewsclient.domain.usecases.GetCommentsUseCase
import com.grebnev.vknewsclient.extensions.mergeWith
import com.grebnev.vknewsclient.presentation.ErrorMessageProvider
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

class CommentsViewModel @Inject constructor(
    feedPost: FeedPost,
    private val getCommentsUseCase: GetCommentsUseCase,
    private val errorMessage: ErrorMessageProvider
) : ViewModel() {

    private val loadNextDataFlow = MutableSharedFlow<CommentsScreenState>()

    val screenState = getCommentsUseCase.getCommentsPost(feedPost)
        .map { postCommentsState ->
            mapCommentsStateToScreenState(feedPost, postCommentsState as PostCommentState)
        }
        .onStart { CommentsScreenState.Loading }
        .mergeWith(loadNextDataFlow)

    private fun mapCommentsStateToScreenState(
        feedPost: FeedPost,
        postCommentState: PostCommentState
    ): CommentsScreenState {
        return when (postCommentState) {
            is PostCommentState.Comments ->
                CommentsScreenState.Comments(feedPost, postCommentState.comments)

            is PostCommentState.Error ->
                CommentsScreenState.Error(
                    errorMessage.getErrorMessage(postCommentState.type)
                )

            is PostCommentState.Initial ->
                CommentsScreenState.Loading

            is PostCommentState.NoComments ->
                CommentsScreenState.NoComments
        }
    }

    fun refreshedCommentsPost() {
        viewModelScope.launch {
            loadNextDataFlow.emit(CommentsScreenState.Loading)
            getCommentsUseCase.retry()
        }
    }
}