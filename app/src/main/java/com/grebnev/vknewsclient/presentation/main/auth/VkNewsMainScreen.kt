package com.grebnev.vknewsclient.presentation.main.auth

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import com.grebnev.vknewsclient.di.keys.NewsFeedType
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
    val backstackState = navigationState.navHostController.currentBackStackEntryAsState()
    val currentDestination = backstackState.value?.destination

    Scaffold(
        bottomBar = {
            NavigationBottomBar(
                navigationState = navigationState,
                currentDestination = currentDestination,
            )
        },
        content = { paddingValues ->
            AppNavGraph(
                navHostController = navigationState.navHostController,
                recommendationsFeedScreenContent = {
                    RecommendationsFeedScreen(
                        paddingValues = paddingValues,
                        onCommentClickListener = {
                            navigationState.navigateToComments(
                                feedPost = it,
                                newsFeedType = NewsFeedType.RECOMMENDATIONS,
                            )
                        },
                    )
                },
                recommendationsCommentsScreenContent = { feedPost ->
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
                            navigationState.navigateToComments(
                                feedPost = it,
                                newsFeedType = NewsFeedType.SUBSCRIPTIONS,
                            )
                        },
                    )
                },
                subscriptionsCommentsScreenContent = { feedPost ->
                    CommentsScreen(
                        feedPost = feedPost,
                        onBackPressed = {
                            navigationState.navHostController.popBackStack()
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
private fun NavigationBottomBar(
    navigationState: NavigationState,
    currentDestination: NavDestination?,
) {
    NavigationBar {
        val items =
            listOf(
                NavigationItem.Home,
                NavigationItem.Favourite,
                NavigationItem.Profile,
            )
        items.forEach { item ->
            val selected =
                currentDestination?.hierarchy?.any {
                    it.route?.startsWith(item.screen.route.substringBefore("/")) == true
                } ?: false

            NavigationBarItem(
                selected = selected,
                onClick = {
                    if (!selected) {
                        navigationState.navigateTo(
                            route = item.screen.route,
                            currentRoute = currentDestination?.route,
                        )
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