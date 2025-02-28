package com.grebnev.vknewsclient.presentation.news.subscriptions

import androidx.lifecycle.viewModelScope
import com.grebnev.vknewsclient.data.ErrorHandlingValues
import com.grebnev.vknewsclient.di.keys.NewsFeedType
import com.grebnev.vknewsclient.domain.entity.FeedPost
import com.grebnev.vknewsclient.domain.state.FeedPostState
import com.grebnev.vknewsclient.domain.usecases.ChangeLikeStatusUseCase
import com.grebnev.vknewsclient.domain.usecases.ChangeSubscriptionStatusUseCase
import com.grebnev.vknewsclient.domain.usecases.DeletePostUseCase
import com.grebnev.vknewsclient.domain.usecases.GetSubscriptionPostsUseCase
import com.grebnev.vknewsclient.domain.usecases.LoadNextDataUseCase
import com.grebnev.vknewsclient.extensions.mergeWith
import com.grebnev.vknewsclient.presentation.ErrorMessageProvider
import com.grebnev.vknewsclient.presentation.news.base.NewsFeedViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class SubscriptionsFeedViewModel @Inject constructor(
    private val getSubscriptionPostsUseCase: GetSubscriptionPostsUseCase,
    private val loadNextDataUseCase: LoadNextDataUseCase,
    private val changeLikeStatusUseCase: ChangeLikeStatusUseCase,
    private val deletePostUseCase: DeletePostUseCase,
    private val changeSubscriptionStatusUseCase: ChangeSubscriptionStatusUseCase,
    private val errorMessageProvider: ErrorMessageProvider
) : NewsFeedViewModel() {

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Timber.e("Coroutine exception handler was called")
        viewModelScope.launch {
            val typeError = ErrorHandlingValues.getTypeError(throwable)
            _errorMessage.value = errorMessageProvider.getErrorMessage(typeError)
        }
    }

    private val subscriptionsFlow = getSubscriptionPostsUseCase()

    private val loadNextDataFlow = MutableSharedFlow<SubscriptionsScreenState>()

    val screenState = subscriptionsFlow
        .map { mapSubscriptionsStateToScreenState(it) }
        .onStart { SubscriptionsScreenState.Loading }
        .mergeWith(loadNextDataFlow)
        .catch { throwable ->
            Timber.e(throwable.message)
            emit(SubscriptionsScreenState.Error(throwable.message ?: "Unknown error"))
        }

    private fun mapSubscriptionsStateToScreenState(
        subscriptionState: FeedPostState
    ): SubscriptionsScreenState {
        return when (subscriptionState) {
            is FeedPostState.Posts -> {
                val currentFeedPosts = subscriptionState.posts
                if (currentFeedPosts.isNotEmpty()) {
                    SubscriptionsScreenState.Posts(posts = currentFeedPosts)
                } else {
                    SubscriptionsScreenState.Loading
                }
            }

            is FeedPostState.NoPosts ->
                SubscriptionsScreenState.NoSubscriptions

            is FeedPostState.Error ->
                SubscriptionsScreenState.Error(
                    errorMessageProvider.getErrorMessage(subscriptionState.type)
                )
        }
    }

    private fun loadNextSubscriptions() {
        viewModelScope.launch(exceptionHandler) {
            loadNextDataFlow.emit(
                SubscriptionsScreenState.Posts(
                    posts = (subscriptionsFlow.value as FeedPostState.Posts).posts,
                    nextDataLoading = true
                )
            )
            loadNextDataUseCase(NewsFeedType.SUBSCRIPTIONS)
        }
    }

    override fun loadNextPosts() {
        loadNextSubscriptions()
    }

    override fun changeLikeStatus(feedPost: FeedPost) {
        viewModelScope.launch(exceptionHandler) {
            changeLikeStatusUseCase(feedPost, NewsFeedType.SUBSCRIPTIONS)
        }
    }

    override fun changeSubscriptionStatus(feedPost: FeedPost) {
        viewModelScope.launch(exceptionHandler) {
            changeSubscriptionStatusUseCase(feedPost, NewsFeedType.SUBSCRIPTIONS)
        }
    }

    override fun delete(feedPost: FeedPost) {
        viewModelScope.launch(exceptionHandler) {
            deletePostUseCase(feedPost, NewsFeedType.SUBSCRIPTIONS)
        }
    }
}