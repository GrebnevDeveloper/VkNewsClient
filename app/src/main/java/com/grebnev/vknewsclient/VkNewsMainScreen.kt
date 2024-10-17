package com.grebnev.vknewsclient

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import com.grebnev.vknewsclient.domain.FeedPost


@Composable
fun VkNewsMainScreen(
    viewModel: VkNewsMainScreenViewModel
) {
    val feedPost = viewModel.feedPost.observeAsState(FeedPost())

    Scaffold(
        topBar = { TopBar() },
        bottomBar = { NavigationBottomBar() },
        content = { paddingValues ->
            PostCard(
                paddingValues = paddingValues,
                feedPost = feedPost.value,
                onViewsClickListener = viewModel::updateCount,
                onSharesClickListener = viewModel::updateCount,
                onCommentsClickListener = viewModel::updateCount,
                onLikesClickListener = viewModel::updateCount,
            )
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