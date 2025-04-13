package com.grebnev.vknewsclient.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.grebnev.vknewsclient.di.keys.NewsFeedType
import com.grebnev.vknewsclient.domain.entity.FeedPost
import com.grebnev.vknewsclient.navigation.Screen.Companion.ROUTE_RECOMMENDATIONS_COMMENTS_FOR_ARGS
import com.grebnev.vknewsclient.navigation.Screen.Companion.ROUTE_SUBSCRIPTIONS_COMMENTS_FOR_ARGS

class NavigationState(
    val navHostController: NavHostController,
) {
    fun navigateTo(
        route: String,
        currentRoute: String?,
    ) {
        if (currentRoute?.contains(ROUTE_RECOMMENDATIONS_COMMENTS_FOR_ARGS) == true ||
            currentRoute?.contains(ROUTE_SUBSCRIPTIONS_COMMENTS_FOR_ARGS) == true
        ) {
            navHostController.popBackStack()
        }

        navHostController.navigate(route) {
            popUpTo(navHostController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    fun navigateToComments(
        feedPost: FeedPost,
        newsFeedType: NewsFeedType,
    ) {
        when (newsFeedType) {
            NewsFeedType.RECOMMENDATIONS ->
                navHostController.navigate(Screen.RecommendationsComments.getRouteWithArgs(feedPost))
            NewsFeedType.SUBSCRIPTIONS ->
                navHostController.navigate(Screen.SubscriptionsComments.getRouteWithArgs(feedPost))
        }
    }
}

@Composable
fun rememberNavigationState(
    navHostController: NavHostController = rememberNavController(),
): NavigationState =
    remember {
        NavigationState(navHostController)
    }