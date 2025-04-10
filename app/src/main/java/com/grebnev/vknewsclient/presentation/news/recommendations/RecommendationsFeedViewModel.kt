package com.grebnev.vknewsclient.presentation.news.recommendations

import androidx.lifecycle.viewModelScope
import com.grebnev.vknewsclient.core.handlers.ErrorHandler
import com.grebnev.vknewsclient.core.wrappers.ErrorType
import com.grebnev.vknewsclient.core.wrappers.ResultStatus
import com.grebnev.vknewsclient.di.keys.NewsFeedType
import com.grebnev.vknewsclient.domain.entity.FeedPost
import com.grebnev.vknewsclient.domain.usecases.ChangeLikeStatusUseCase
import com.grebnev.vknewsclient.domain.usecases.ChangeSubscriptionStatusUseCase
import com.grebnev.vknewsclient.domain.usecases.DeletePostUseCase
import com.grebnev.vknewsclient.domain.usecases.GetRecommendationsUseCase
import com.grebnev.vknewsclient.domain.usecases.LoadNextDataUseCase
import com.grebnev.vknewsclient.presentation.base.ErrorMessageProvider
import com.grebnev.vknewsclient.presentation.news.base.NewsFeedViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class RecommendationsFeedViewModel
    @Inject
    constructor(
        private val getRecommendationsUseCase: GetRecommendationsUseCase,
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

        private val recommendationsFlow = getRecommendationsUseCase()

        val screenState =
            recommendationsFlow
                .map { mapResultStateToScreenState(it) }
                .onStart { emit(RecommendationsFeedScreenState.Loading) }
                .catch { cause ->
                    RecommendationsFeedScreenState.Error(cause.message ?: "Unknown error")
                }.stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.Lazily,
                    initialValue = RecommendationsFeedScreenState.Initial,
                )

        private fun mapResultStateToScreenState(
            recommendationsState: ResultStatus<List<FeedPost>, ErrorType>,
        ): RecommendationsFeedScreenState =
            when (recommendationsState) {
                is ResultStatus.Error -> {
                    RecommendationsFeedScreenState.Error(
                        errorMessageProvider.getErrorMessage(recommendationsState.error),
                    )
                }

                is ResultStatus.Empty ->
                    RecommendationsFeedScreenState.NoRecommendations

                is ResultStatus.Success -> {
                    val currentFeedPost = recommendationsState.data
                    if (currentFeedPost.isNotEmpty()) {
                        RecommendationsFeedScreenState.Posts(
                            posts = currentFeedPost,
                            nextDataLoading = recommendationsState.nextDataLoading,
                        )
                    } else {
                        RecommendationsFeedScreenState.Loading
                    }
                }
            }

        override fun loadNextPosts() {
            viewModelScope.launch(exceptionHandler) {
                loadNextDataUseCase(NewsFeedType.RECOMMENDATIONS)
            }
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

        override fun onCleared() {
            super.onCleared()
            getRecommendationsUseCase.close()
        }
    }