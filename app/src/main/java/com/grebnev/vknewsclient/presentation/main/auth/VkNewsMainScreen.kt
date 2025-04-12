package com.grebnev.vknewsclient.presentation.main.auth

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import com.grebnev.vknewsclient.navigation.AppNavGraph
import com.grebnev.vknewsclient.navigation.NavigationState
import com.grebnev.vknewsclient.navigation.rememberNavigationState
import com.grebnev.vknewsclient.presentation.comments.CommentsScreen
import com.grebnev.vknewsclient.presentation.main.NavigationItem
import com.grebnev.vknewsclient.presentation.news.recommendations.RecommendationsFeedScreen
import com.grebnev.vknewsclient.presentation.news.subscriptions.SubscriptionsFeedScreen
import com.grebnev.vknewsclient.presentation.profile.ProfileInfoScreen

@Composable
fun VkNewsMainScreen(onLogout: () -> Unit) {
    val navigationState = rememberNavigationState()

    Scaffold(
        bottomBar = { NavigationBottomBar(navigationState) },
        content = { paddingValues ->
            AppNavGraph(
                navHostController = navigationState.navHostController,
                recommendationsFeedScreenContent = {
                    RecommendationsFeedScreen(
                        paddingValues = paddingValues,
                        onCommentClickListener = {
                            navigationState.navigateToComments(it)
                        },
                    )
                },
                commentsScreenContent = { feedPost ->
                    CommentsScreen(
                        feedPost = feedPost,
                        onBackPressed = {
                            navigationState.navHostController.popBackStack()
                        },
                    )
                },
                subscriptionsFeedScreenContent = {
                    SubscriptionsFeedScreen(
                        paddingValues = paddingValues,
                        onCommentClickListener = {
                            navigationState.navigateToComments(it)
                        },
                    )
                },
                profileScreenContent = {
                    ProfileInfoScreen(onLogout = onLogout)
                },
            )
        },
    )
}

@Composable
private fun NavigationBottomBar(navigationState: NavigationState) {
    val navBackStackEntry by navigationState.navHostController.currentBackStackEntryAsState()

    NavigationBar {
        val items =
            listOf(
                NavigationItem.Home,
                NavigationItem.Favourite,
                NavigationItem.Profile,
            )
        items.forEach { item ->
            val selected =
                navBackStackEntry?.destination?.hierarchy?.any {
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
                },
            )
        }
    }
}