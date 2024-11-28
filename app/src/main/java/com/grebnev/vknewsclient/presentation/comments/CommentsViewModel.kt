package com.grebnev.vknewsclient.presentation.comments

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.grebnev.vknewsclient.R
import com.grebnev.vknewsclient.domain.FeedPost
import com.grebnev.vknewsclient.domain.PostComment

class CommentsViewModel(
    feedPost: FeedPost
) : ViewModel() {

    private val comments = mutableListOf<PostComment>().apply {
        repeat(10) {
            add(
                PostComment(
                    id = it,
                    avatarResId = R.drawable.avatar_author_sample,
                    authorName = "Author",
                    commentText = "Long comment text",
                    publicationDate = "14:00"
                )
            )
        }
    }

    private val initialState = CommentsScreenState.Initial

    private val _screenState = MutableLiveData<CommentsScreenState>(initialState)
    val screenState: LiveData<CommentsScreenState> = _screenState

    init {
        loadComments(feedPost)
    }

    private fun loadComments(feedPost: FeedPost) {
        _screenState.value = CommentsScreenState.Comments(feedPost = feedPost, comments = comments)
    }

    fun closeComments() {
    }
}