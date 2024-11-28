package com.grebnev.vknewsclient.presentation.main

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import com.grebnev.vknewsclient.domain.FeedPost
import com.grebnev.vknewsclient.navigation.AppNavGraph
import com.grebnev.vknewsclient.navigation.NavigationState
import com.grebnev.vknewsclient.navigation.rememberNavigationState
import com.grebnev.vknewsclient.presentation.comments.CommentsScreen
import com.grebnev.vknewsclient.presentation.news.NewsFeedScreen


@Composable
fun VkNewsMainScreen() {
    val navigationState = rememberNavigationState()

    val commentsToPost: MutableState<FeedPost?> = remember {
        mutableStateOf(null)
    }


    Scaffold(
        bottomBar = { NavigationBottomBar(navigationState) },
        content = { paddingValues ->
            AppNavGraph(
                navHostController = navigationState.navHostController,
                newsFeedScreenContent = {
                    NewsFeedScreen(
                        paddingValues = paddingValues,
                        onCommentClickListener = {
                            navigationState.navigateToComments(it)
                        }
                    )
                },
                commentsScreenContent = { feedPost ->
                    CommentsScreen(
                        feedPost = feedPost,
                        onBackPressed = {
                            navigationState.navHostController.popBackStack()
                        }
                    )
                },
                favouriteScreenContent = {
                    TextCounter("Favourite")
                },
                profileScreenContent = {
                    TextCounter("Profile")
                }
            )
        }
    )
}

@Composable
private fun TextCounter(name: String) {
    var count by rememberSaveable {
        mutableIntStateOf(0)
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            modifier = Modifier.clickable { count++ },
            text = "$name count: $count"
        )
    }

}

@Composable
private fun NavigationBottomBar(
    navigationState: NavigationState
) {

    val navBackStackEntry by navigationState.navHostController.currentBackStackEntryAsState()

    NavigationBar {
        val items = listOf(
            NavigationItem.Home,
            NavigationItem.Favourite,
            NavigationItem.Profile
        )
        items.forEach() { item ->
            val selected = navBackStackEntry?.destination?.hierarchy?.any() {
                it.route == item.screen.route
            } ?: false

            NavigationBarItem(
                selected = selected,
                onClick = {
                    if (!selected) {
                        navigationState.navigateTo(item.screen.route)
                    }
                },
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