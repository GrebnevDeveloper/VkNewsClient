package com.grebnev.vknewsclient.presentation.news.subscriptions

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grebnev.vknewsclient.di.keys.NewsFeedType
import com.grebnev.vknewsclient.domain.entity.FeedPost
import com.grebnev.vknewsclient.domain.usecases.ChangeLikeStatusUseCase
import com.grebnev.vknewsclient.domain.usecases.ChangeSubscriptionStatusUseCase
import com.grebnev.vknewsclient.domain.usecases.DeletePostUseCase
import com.grebnev.vknewsclient.domain.usecases.GetSubscriptionPostsUseCase
import com.grebnev.vknewsclient.domain.usecases.LoadNextDataUseCase
import com.grebnev.vknewsclient.extensions.mergeWith
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

class SubscriptionsFeedViewModel @Inject constructor(
    private val getSubscriptionPostsUseCase: GetSubscriptionPostsUseCase,
    private val loadNextDataUseCase: LoadNextDataUseCase,
    private val changeLikeStatusUseCase: ChangeLikeStatusUseCase,
    private val deletePostUseCase: DeletePostUseCase,
    private val changeSubscriptionStatusUseCase: ChangeSubscriptionStatusUseCase
) : ViewModel() {

    private val exceptionHandler = CoroutineExceptionHandler { _, _ ->
        Log.d("NewsFeedViewModel", "Coroutine exception handler was called")
    }

    private val subscriptionsFlow = getSubscriptionPostsUseCase()

    private val loadNextDataFlow = MutableSharedFlow<SubscriptionsScreenState>()

    val screenState = subscriptionsFlow
        .onEach { if (it.isEmpty()) loadNextDataFlow.emit(SubscriptionsScreenState.NoSubscriptions) }
        .filter { it.isNotEmpty() }
        .map { SubscriptionsScreenState.Posts(posts = it) as SubscriptionsScreenState }
        .onStart { emit(SubscriptionsScreenState.Loading) }
        .mergeWith(loadNextDataFlow)

    fun loadNextSubscriptions() {
        viewModelScope.launch {
            loadNextDataFlow.emit(
                SubscriptionsScreenState.Posts(
                    posts = subscriptionsFlow.value,
                    nextDataLoading = true
                )
            )
            loadNextDataUseCase(NewsFeedType.SUBSCRIPTIONS)
        }
    }

    fun changeLikeStatus(feedPost: FeedPost) {
        viewModelScope.launch(exceptionHandler) {
            changeLikeStatusUseCase(feedPost, NewsFeedType.SUBSCRIPTIONS)
        }
    }

    fun changeSubscriptionStatus(feedPost: FeedPost) {
        viewModelScope.launch(exceptionHandler) {
            changeSubscriptionStatusUseCase(feedPost, NewsFeedType.SUBSCRIPTIONS)
        }
    }

    fun delete(feedPost: FeedPost) {
        viewModelScope.launch(exceptionHandler) {
            deletePostUseCase(feedPost, NewsFeedType.SUBSCRIPTIONS)
        }
    }
}