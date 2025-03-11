package com.grebnev.vknewsclient.presentation.news.base

import androidx.lifecycle.ViewModel
import com.grebnev.vknewsclient.domain.entity.FeedPost
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

abstract class NewsFeedViewModel : ViewModel() {
    protected val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> get() = _errorMessage

    abstract fun changeLikeStatus(feedPost: FeedPost)

    abstract fun changeSubscriptionStatus(feedPost: FeedPost)

    abstract fun delete(feedPost: FeedPost)

    abstract fun loadNextPosts()

    fun resetErrorMessage() {
        _errorMessage.value = null
    }
}