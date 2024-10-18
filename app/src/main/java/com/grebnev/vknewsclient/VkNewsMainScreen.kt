package com.grebnev.vknewsclient

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp


@Composable
fun VkNewsMainScreen(
    viewModel: VkNewsMainScreenViewModel
) {
    val feedPostList = viewModel.feedPostList.observeAsState(listOf())

    Scaffold(
        topBar = { TopBar() },
        bottomBar = { NavigationBottomBar() },
        content = { paddingValues ->
            LazyColumn(
                modifier = Modifier.padding(paddingValues),
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                items(feedPostList.value, key = {it.id}) { feedPost ->
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
                            onCommentsClickListener = { statisticItem ->
                                viewModel.updateCount(feedPost, statisticItem)
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

@Composable
private fun NavigationBottomBar() {
    NavigationBar {
        val selectedItemPosition = remember {
            mutableIntStateOf(0)
        }
        val items = listOf(
            NavigationItem.Home,
            NavigationItem.Favourite,
            NavigationItem.Profile
        )
        items.forEachIndexed() { index, item ->
            NavigationBarItem(
                selected = selectedItemPosition.intValue == index,
                onClick = {selectedItemPosition.intValue = index},
                icon = {
                    Icon(imageVector = item.icon, contentDescription = null)
                },
                label = {
                    Text(stringResource(item.titleResId))
                }
            )
        }
    }
}