package com.grebnev.vknewsclient.presentation.news.recommendations

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.grebnev.vknewsclient.domain.entity.FeedPost
import com.grebnev.vknewsclient.presentation.getApplicationComponent
import com.grebnev.vknewsclient.presentation.news.PostCard
import com.grebnev.vknewsclient.ui.theme.DarkBlue

@Composable
fun RecommendationsFeedScreen(
    paddingValues: PaddingValues,
    onCommentClickListener: (FeedPost) -> Unit
) {
    val component = getApplicationComponent()
    val viewModel: RecommendationsFeedViewModel = viewModel(factory = component.getViewModelFactory())
    val screenState = viewModel.screenState.collectAsState(RecommendationsFeedScreenState.Initial)
    RecommendationsFeedScreenContent(
        screenState = screenState,
        paddingValues = paddingValues,
        onCommentClickListener = onCommentClickListener,
        viewModel = viewModel
    )
}

@Composable
private fun RecommendationsFeedScreenContent(
    screenState: State<RecommendationsFeedScreenState>,
    paddingValues: PaddingValues,
    onCommentClickListener: (FeedPost) -> Unit,
    viewModel: RecommendationsFeedViewModel
) {
    when (val currentState = screenState.value) {
        is RecommendationsFeedScreenState.Posts -> {
            FeedPosts(
                viewModel = viewModel,
                paddingValues = paddingValues,
                posts = currentState.posts.distinctBy { it.id },
                onCommentClickListener = onCommentClickListener,
                nextDataIsLoading = currentState.nextDataLoading
            )
        }

        is RecommendationsFeedScreenState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = DarkBlue)
            }
        }

        is RecommendationsFeedScreenState.Initial -> {}
    }
}

@Composable
private fun FeedPosts(
    viewModel: RecommendationsFeedViewModel,
    paddingValues: PaddingValues,
    posts: List<FeedPost>,
    onCommentClickListener: (FeedPost) -> Unit,
    nextDataIsLoading: Boolean
) {
    Scaffold(
        topBar = { TopBar() },
        content = { paddingValues ->
            LazyColumn(
                modifier = Modifier.padding(paddingValues),
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                items(posts, key = { it.id }) { feedPost ->
                    val dismissState = rememberSwipeToDismissBoxState()

                    if (dismissState.currentValue == SwipeToDismissBoxValue.EndToStart) {
                        viewModel.delete(feedPost)
                    }
                    SwipeToDismissBox(
                        modifier = Modifier.animateItem(),
                        state = dismissState,
                        enableDismissFromStartToEnd = false,
                        backgroundContent = {}
                    ) {
                        PostCard(
                            feedPost = feedPost,
                            onCommentsClickListener = {
                                onCommentClickListener(feedPost)
                            },
                            onLikesClickListener = { _ ->
                                viewModel.changeLikeStatus(feedPost)
                            },
                            onSubscribeClickListener = {
                                viewModel.changeSubscriptionStatus(feedPost)
                            }
                        )
                    }
                }
                item {
                    if (nextDataIsLoading) {
                        Box(
                            modifier = Modifier
                                .padding(bottom = 100.dp)
                                .fillMaxWidth()
                                .wrapContentHeight(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = DarkBlue)
                        }
                    } else {
                        SideEffect {
                            viewModel.loadNextRecommendations()
                        }
                    }
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar() {
    TopAppBar(
        title = {
            Text(text = "Recommendations")
        }
    )
}