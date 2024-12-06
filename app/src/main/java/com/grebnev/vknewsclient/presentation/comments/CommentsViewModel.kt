package com.grebnev.vknewsclient.presentation.comments

import androidx.lifecycle.ViewModel
import com.grebnev.vknewsclient.data.repository.NewsFeedRepository
import com.grebnev.vknewsclient.domain.FeedPost
import kotlinx.coroutines.flow.map

class CommentsViewModel(
    feedPost: FeedPost
) : ViewModel() {

    private val repository = NewsFeedRepository()

    val screenState = repository.loadComments(feedPost)
        .map {
            CommentsScreenState.Comments(
                feedPost = feedPost,
                comments = it
            )
        }

}