package com.grebnev.vknewsclient.presentation.comments

import androidx.lifecycle.ViewModel
import com.grebnev.vknewsclient.data.repository.NewsFeedRepositoryImpl
import com.grebnev.vknewsclient.domain.entity.FeedPost
import com.grebnev.vknewsclient.domain.usecases.GetCommentsUseCase
import kotlinx.coroutines.flow.map

class CommentsViewModel(
    feedPost: FeedPost
) : ViewModel() {

    private val repository = NewsFeedRepositoryImpl()
    private val getCommentsUseCase = GetCommentsUseCase(repository)

    val screenState = getCommentsUseCase(feedPost)
        .map {
            CommentsScreenState.Comments(
                feedPost = feedPost,
                comments = it
            )
        }

}