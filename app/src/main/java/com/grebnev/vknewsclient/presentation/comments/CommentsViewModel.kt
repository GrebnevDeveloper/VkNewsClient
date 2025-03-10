package com.grebnev.vknewsclient.presentation.comments

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grebnev.vknewsclient.core.extensions.mergeWith
import com.grebnev.vknewsclient.core.wrappers.ErrorType
import com.grebnev.vknewsclient.core.wrappers.ResultState
import com.grebnev.vknewsclient.domain.entity.FeedPost
import com.grebnev.vknewsclient.domain.entity.PostComment
import com.grebnev.vknewsclient.domain.usecases.GetCommentsUseCase
import com.grebnev.vknewsclient.presentation.base.ErrorMessageProvider
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

class CommentsViewModel
    @Inject
    constructor(
        feedPost: FeedPost,
        private val getCommentsUseCase: GetCommentsUseCase,
        private val errorMessage: ErrorMessageProvider,
    ) : ViewModel() {
        private val loadNextDataFlow = MutableSharedFlow<CommentsScreenState>()

        val screenState =
            getCommentsUseCase
                .getCommentsPost(feedPost)
                .map { postCommentsState ->
                    mapCommentsStateToScreenState(feedPost, postCommentsState)
                }.onStart { CommentsScreenState.Loading }
                .mergeWith(loadNextDataFlow)

        private fun mapCommentsStateToScreenState(
            feedPost: FeedPost,
            postCommentState: ResultState<List<PostComment>, ErrorType>,
        ): CommentsScreenState =
            when (postCommentState) {
                is ResultState.Success ->
                    CommentsScreenState.Comments(feedPost, postCommentState.data)

                is ResultState.Error ->
                    CommentsScreenState.Error(
                        errorMessage.getErrorMessage(postCommentState.error),
                    )

                is ResultState.Initial ->
                    CommentsScreenState.Loading

                is ResultState.Empty ->
                    CommentsScreenState.NoComments
            }

        fun refreshedCommentsPost() {
            viewModelScope.launch {
                loadNextDataFlow.emit(CommentsScreenState.Loading)
                getCommentsUseCase.retry()
            }
        }
    }