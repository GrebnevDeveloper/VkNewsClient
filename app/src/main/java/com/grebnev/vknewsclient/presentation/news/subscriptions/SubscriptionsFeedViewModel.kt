package com.grebnev.vknewsclient.presentation.news.subscriptions

import androidx.lifecycle.viewModelScope
import com.grebnev.vknewsclient.core.extensions.mergeWith
import com.grebnev.vknewsclient.core.handlers.ErrorHandler
import com.grebnev.vknewsclient.core.wrappers.ErrorType
import com.grebnev.vknewsclient.core.wrappers.ResultStatus
import com.grebnev.vknewsclient.di.keys.NewsFeedType
import com.grebnev.vknewsclient.domain.entity.FeedPost
import com.grebnev.vknewsclient.domain.usecases.ChangeLikeStatusUseCase
import com.grebnev.vknewsclient.domain.usecases.ChangeSubscriptionStatusUseCase
import com.grebnev.vknewsclient.domain.usecases.DeletePostUseCase
import com.grebnev.vknewsclient.domain.usecases.GetSubscriptionPostsUseCase
import com.grebnev.vknewsclient.domain.usecases.LoadNextDataUseCase
import com.grebnev.vknewsclient.presentation.base.ErrorMessageProvider
import com.grebnev.vknewsclient.presentation.news.base.NewsFeedViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class SubscriptionsFeedViewModel
    @Inject
    constructor(
        private val getSubscriptionPostsUseCase: GetSubscriptionPostsUseCase,
        private val loadNextDataUseCase: LoadNextDataUseCase,
        private val changeLikeStatusUseCase: ChangeLikeStatusUseCase,
        private val deletePostUseCase: DeletePostUseCase,
        private val changeSubscriptionStatusUseCase: ChangeSubscriptionStatusUseCase,
        private val errorMessageProvider: ErrorMessageProvider,
    ) : NewsFeedViewModel() {
        private val exceptionHandler =
            CoroutineExceptionHandler { _, throwable ->
                Timber.e("Coroutine exception handler was called")
                viewModelScope.launch {
                    val typeError = ErrorHandler.getErrorType(throwable)
                    _errorMessage.value = errorMessageProvider.getErrorMessage(typeError)
                }
            }

        private val subscriptionsFlow = getSubscriptionPostsUseCase()

        private val loadNextDataFlow = MutableSharedFlow<SubscriptionsScreenState>()

        val screenState =
            subscriptionsFlow
                .map { mapResultStateToScreenState(it) }
                .onStart { SubscriptionsScreenState.Loading }
                .mergeWith(loadNextDataFlow)
                .catch { throwable ->
                    Timber.e(throwable)
                    SubscriptionsScreenState.Error(throwable.message ?: "Unknown error")
                }

        private fun mapResultStateToScreenState(
            subscriptionState: ResultStatus<List<FeedPost>, ErrorType>,
        ): SubscriptionsScreenState =
            when (subscriptionState) {
                is ResultStatus.Success -> {
                    val currentFeedPosts = subscriptionState.data
                    if (currentFeedPosts.isNotEmpty()) {
                        SubscriptionsScreenState.Posts(posts = currentFeedPosts)
                    } else {
                        SubscriptionsScreenState.Loading
                    }
                }

                is ResultStatus.Empty ->
                    SubscriptionsScreenState.NoSubscriptions

                is ResultStatus.Error ->
                    SubscriptionsScreenState.Error(
                        errorMessageProvider.getErrorMessage(subscriptionState.error),
                    )
            }

        private fun loadNextSubscriptions() {
            viewModelScope.launch(exceptionHandler) {
                loadNextDataFlow.emit(
                    SubscriptionsScreenState.Posts(
                        posts = (subscriptionsFlow.value as ResultStatus.Success).data,
                        nextDataLoading = true,
                    ),
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