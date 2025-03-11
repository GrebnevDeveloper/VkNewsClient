package com.grebnev.vknewsclient.presentation.news.recommendations

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.grebnev.vknewsclient.R
import com.grebnev.vknewsclient.domain.entity.FeedPost
import com.grebnev.vknewsclient.presentation.base.ErrorScreenWithLoading
import com.grebnev.vknewsclient.presentation.base.LoadingIndicator
import com.grebnev.vknewsclient.presentation.getApplicationComponent
import com.grebnev.vknewsclient.presentation.news.base.FeedPosts

@Composable
fun RecommendationsFeedScreen(
    paddingValues: PaddingValues,
    onCommentClickListener: (FeedPost) -> Unit,
) {
    val component = getApplicationComponent()
    val viewModel: RecommendationsFeedViewModel =
        viewModel(factory = component.getViewModelFactory())
    val screenState = viewModel.screenState.collectAsState(RecommendationsFeedScreenState.Initial)
    RecommendationsFeedScreenContent(
        screenState = screenState,
        paddingValues = paddingValues,
        onCommentClickListener = onCommentClickListener,
        viewModel = viewModel,
    )
}

@Composable
private fun RecommendationsFeedScreenContent(
    screenState: State<RecommendationsFeedScreenState>,
    paddingValues: PaddingValues,
    onCommentClickListener: (FeedPost) -> Unit,
    viewModel: RecommendationsFeedViewModel,
) {
    when (val currentState = screenState.value) {
        is RecommendationsFeedScreenState.Posts -> {
            FeedPosts(
                viewModel = viewModel,
                paddingValues = paddingValues,
                posts = currentState.posts.distinctBy { it.id },
                onCommentClickListener = onCommentClickListener,
                nextDataIsLoading = currentState.nextDataLoading,
                titleTopBar = stringResource(R.string.recommendations),
            )
        }

        is RecommendationsFeedScreenState.Loading -> {
            LoadingIndicator()
        }

        is RecommendationsFeedScreenState.NoRecommendations -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Text(stringResource(R.string.no_recommendations))
            }
        }

        is RecommendationsFeedScreenState.Error -> {
            ErrorScreenWithLoading(
                errorMessage = currentState.message,
            )
        }

        is RecommendationsFeedScreenState.Initial -> {}
    }
}