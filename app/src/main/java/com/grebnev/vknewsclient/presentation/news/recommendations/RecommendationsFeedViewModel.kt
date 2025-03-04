package com.grebnev.vknewsclient.presentation.news.recommendations

import androidx.lifecycle.viewModelScope
import com.grebnev.vknewsclient.core.wrappers.ErrorType
import com.grebnev.vknewsclient.core.wrappers.ResultState
import com.grebnev.vknewsclient.core.handlers.ErrorHandler
import com.grebnev.vknewsclient.di.keys.NewsFeedType
import com.grebnev.vknewsclient.domain.entity.FeedPost
import com.grebnev.vknewsclient.domain.usecases.ChangeLikeStatusUseCase
import com.grebnev.vknewsclient.domain.usecases.ChangeSubscriptionStatusUseCase
import com.grebnev.vknewsclient.domain.usecases.DeletePostUseCase
import com.grebnev.vknewsclient.domain.usecases.GetRecommendationsUseCase
import com.grebnev.vknewsclient.domain.usecases.LoadNextDataUseCase
import com.grebnev.vknewsclient.core.extensions.mergeWith
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
            val typeError = ErrorHandler.getErrorType(throwable)
            _errorMessage.value = errorMessageProvider.getErrorMessage(typeError)
        }
    }

    private val recommendationsFlow = getRecommendationsUseCase()

    private val loadNextDataFlow = MutableSharedFlow<RecommendationsFeedScreenState>()

    val screenState = recommendationsFlow
        .map { mapResultStateToScreenState(it) }
        .onStart { RecommendationsFeedScreenState.Loading }
        .mergeWith(loadNextDataFlow)
        .catch { cause ->
            RecommendationsFeedScreenState.Error(cause.message ?: "Unknown error")
        }

    private fun mapResultStateToScreenState(
        recommendationsState: ResultState<List<FeedPost>, ErrorType>
    ): RecommendationsFeedScreenState {
        return when (recommendationsState) {
            is ResultState.Error -> {
                RecommendationsFeedScreenState.Error(
                    errorMessageProvider.getErrorMessage(recommendationsState.error)
                )
            }

            is ResultState.Empty ->
                RecommendationsFeedScreenState.NoRecommendations

            is ResultState.Initial ->
                RecommendationsFeedScreenState.Loading

            is ResultState.Success -> {
                val currentFeedPost = recommendationsState.data
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
                    posts = (recommendationsFlow.value as ResultState.Success).data ,
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