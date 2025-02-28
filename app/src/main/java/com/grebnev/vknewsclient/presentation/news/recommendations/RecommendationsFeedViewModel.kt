package com.grebnev.vknewsclient.presentation.news.recommendations

import androidx.lifecycle.viewModelScope
import com.grebnev.vknewsclient.data.ErrorHandlingValues
import com.grebnev.vknewsclient.di.keys.NewsFeedType
import com.grebnev.vknewsclient.domain.entity.FeedPost
import com.grebnev.vknewsclient.domain.state.FeedPostState
import com.grebnev.vknewsclient.domain.usecases.ChangeLikeStatusUseCase
import com.grebnev.vknewsclient.domain.usecases.ChangeSubscriptionStatusUseCase
import com.grebnev.vknewsclient.domain.usecases.DeletePostUseCase
import com.grebnev.vknewsclient.domain.usecases.GetRecommendationsUseCase
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

class RecommendationsFeedViewModel @Inject constructor(
    private val getRecommendationsUseCase: GetRecommendationsUseCase,
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

    private val recommendationsFlow = getRecommendationsUseCase()

    private val loadNextDataFlow = MutableSharedFlow<RecommendationsFeedScreenState>()

    val screenState = recommendationsFlow
        .map { mapRecommendationsStateToScreenState(it) }
        .onStart { RecommendationsFeedScreenState.Loading }
        .mergeWith(loadNextDataFlow)
        .catch { cause ->
            emit(RecommendationsFeedScreenState.Error(cause.message ?: "unknown error"))
        }

    private fun mapRecommendationsStateToScreenState(
        recommendationsState: FeedPostState
    ): RecommendationsFeedScreenState {
        return when (recommendationsState) {
            is FeedPostState.Error -> {
                RecommendationsFeedScreenState.Error(
                    errorMessageProvider.getErrorMessage(recommendationsState.type)
                )
            }

            is FeedPostState.NoPosts -> {
                RecommendationsFeedScreenState.Loading
            }

            is FeedPostState.Posts -> {
                val currentFeedPost = recommendationsState.posts
                if (currentFeedPost.isNotEmpty()) {
                    RecommendationsFeedScreenState.Posts(currentFeedPost)
                } else {
                    RecommendationsFeedScreenState.Loading
                }
            }
        }
    }

    private fun loadNextRecommendations() {
        viewModelScope.launch(exceptionHandler) {
            loadNextDataFlow.emit(
                RecommendationsFeedScreenState.Posts(
                    posts = (recommendationsFlow.value as FeedPostState.Posts).posts,
                    nextDataLoading = true
                )
            )
            loadNextDataUseCase(NewsFeedType.RECOMMENDATIONS)
        }
    }

    override fun loadNextPosts() {
        loadNextRecommendations()
    }

    override fun changeLikeStatus(feedPost: FeedPost) {
        viewModelScope.launch(exceptionHandler) {
            changeLikeStatusUseCase(feedPost, NewsFeedType.RECOMMENDATIONS)
        }
    }

    override fun changeSubscriptionStatus(feedPost: FeedPost) {
        viewModelScope.launch(exceptionHandler) {
            changeSubscriptionStatusUseCase(feedPost, NewsFeedType.RECOMMENDATIONS)
        }
    }

    override fun delete(feedPost: FeedPost) {
        viewModelScope.launch(exceptionHandler) {
            deletePostUseCase(feedPost, NewsFeedType.RECOMMENDATIONS)
        }
    }
}