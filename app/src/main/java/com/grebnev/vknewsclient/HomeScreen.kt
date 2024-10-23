package com.grebnev.vknewsclient

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.grebnev.vknewsclient.domain.FeedPost

@Composable
fun HomeScreen(
    paddingValues: PaddingValues,
    onCommentClickListener: (FeedPost) -> Unit
) {
    val viewModel: NewsFeedViewModel = viewModel()
    val screenState = viewModel.screenState.observeAsState(NewsFeedScreenState.Initial)

    when (val currentState = screenState.value) {
        is NewsFeedScreenState.Posts -> {
            FeedPosts(
                viewModel = viewModel,
                paddingValues = paddingValues,
                posts = currentState.posts,
                onCommentClickListener = onCommentClickListener
            )
        }

        is NewsFeedScreenState.Initial -> {

        }
    }


}

@Composable
private fun FeedPosts(
    viewModel: NewsFeedViewModel,
    paddingValues: PaddingValues,
    posts: List<FeedPost>,
    onCommentClickListener: (FeedPost) -> Unit
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
                            onViewsClickListener = { statisticItem ->
                                viewModel.updateCount(feedPost, statisticItem)
                            },
                            onSharesClickListener = { statisticItem ->
                                viewModel.updateCount(feedPost, statisticItem)
                            },
                            onCommentsClickListener = {
                                onCommentClickListener(feedPost)
                            },
                            onLikesClickListener = { statisticItem ->
                                viewModel.updateCount(feedPost, statisticItem)
                            },
                        )
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
            Text(text = "VkNews")
        }
    )
}