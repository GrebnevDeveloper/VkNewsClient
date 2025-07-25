package com.grebnev.vknewsclient.presentation.news.base

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.grebnev.vknewsclient.R
import com.grebnev.vknewsclient.domain.entity.FeedPost
import com.grebnev.vknewsclient.presentation.base.LoadingIndicator
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedPosts(
    viewModel: NewsFeedViewModel,
    paddingValues: PaddingValues,
    posts: List<FeedPost>,
    onCommentClickListener: (FeedPost) -> Unit,
    nextDataIsLoading: Boolean,
    titleTopBar: String,
) {
    val errorMessage by viewModel.errorMessage.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val feedPostState = rememberLazyListState()

    val scrollBehaviorTopBar = TopAppBarDefaults.enterAlwaysScrollBehavior()

    LaunchedEffect(errorMessage) {
        errorMessage?.let { message ->
            coroutineScope.launch {
                snackbarHostState.showSnackbar(message)
                viewModel.resetErrorMessage()
            }
        }
    }

    LaunchedEffect(feedPostState) {
        snapshotFlow { feedPostState.layoutInfo }
            .map { layoutInfo ->
                val lastVisibleItem = layoutInfo.visibleItemsInfo.lastOrNull()
                lastVisibleItem?.index == layoutInfo.totalItemsCount - 3
            }.distinctUntilChanged()
            .collect { needLoadMore ->
                if (needLoadMore && nextDataIsLoading) {
                    viewModel.loadNextPosts()
                }
            }
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehaviorTopBar.nestedScrollConnection),
        topBar = {
            TopBar(
                titleTopBar = titleTopBar,
                scrollBehavior = scrollBehaviorTopBar,
            )
        },
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                modifier =
                    Modifier
                        .padding(bottom = 76.dp),
            )
        },
        content = { paddingValues ->
            LazyColumn(
                state = feedPostState,
                modifier = Modifier.padding(paddingValues),
                verticalArrangement = Arrangement.spacedBy(5.dp),
            ) {
                items(
                    items = posts,
                    key = { it.id },
                    contentType = { "feedPost" },
                ) { feedPost ->
                    val dismissState = rememberSwipeToDismissBoxState()

                    LaunchedEffect(dismissState.currentValue) {
                        if (dismissState.currentValue == SwipeToDismissBoxValue.EndToStart) {
                            viewModel.delete(feedPost)
                            dismissState.reset()
                        }
                    }
                    SwipeToDismissBox(
                        modifier = Modifier.animateItem(),
                        state = dismissState,
                        enableDismissFromStartToEnd = false,
                        backgroundContent = {},
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
                            },
                        )
                    }
                }
                item(contentType = "loading") {
                    if (nextDataIsLoading) {
                        LoadingIndicator(
                            modifier =
                                Modifier
                                    .padding(bottom = 100.dp)
                                    .fillMaxWidth(),
                        )
                    } else {
                        EmptyState()
                    }
                }
            }
        },
    )
}

@Composable
private fun EmptyState(modifier: Modifier = Modifier) {
    Box(
        modifier =
            modifier
                .padding(bottom = 100.dp)
                .fillMaxWidth(),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = stringResource(R.string.no_post_to_display),
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar(
    titleTopBar: String,
    scrollBehavior: TopAppBarScrollBehavior,
) {
    TopAppBar(
        title = {
            Text(titleTopBar)
        },
        scrollBehavior = scrollBehavior,
    )
}