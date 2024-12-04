package com.grebnev.vknewsclient.presentation.comments

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grebnev.vknewsclient.data.repository.NewsFeedRepository
import com.grebnev.vknewsclient.domain.FeedPost
import kotlinx.coroutines.launch

class CommentsViewModel(
    feedPost: FeedPost
) : ViewModel() {

    private val repository = NewsFeedRepository()

    private val initialState = CommentsScreenState.Initial

    private val _screenState = MutableLiveData<CommentsScreenState>(initialState)
    val screenState: LiveData<CommentsScreenState> = _screenState

    init {
        loadComments(feedPost)
    }

    private fun loadComments(feedPost: FeedPost) {
        viewModelScope.launch {
            val comments = repository.loadComments(feedPost)
            _screenState.value =
                CommentsScreenState.Comments(feedPost = feedPost, comments = comments)
        }

    }
}