package com.grebnev.vknewsclient.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.grebnev.vknewsclient.domain.entity.FeedPost

@Composable
fun AppNavGraph(
    navHostController: NavHostController,
    recommendationsFeedScreenContent: @Composable () -> Unit,
    recommendationsCommentsScreenContent: @Composable (FeedPost) -> Unit,
    subscriptionsFeedScreenContent: @Composable () -> Unit,
    subscriptionsCommentsScreenContent: @Composable (FeedPost) -> Unit,
    profileScreenContent: @Composable () -> Unit,
) {
    NavHost(
        navController = navHostController,
        startDestination = Screen.RecommendationsHome.route,
    ) {
        recommendationsScreenNavGraph(
            recommendationsFeedScreenContent = recommendationsFeedScreenContent,
            commentsScreenContent = recommendationsCommentsScreenContent,
        )
        subscriptionsScreenNavGraph(
            subscriptionsFeedScreenContent = subscriptionsFeedScreenContent,
            commentsScreenContent = subscriptionsCommentsScreenContent,
        )
        composable(Screen.Profile.route) {
            profileScreenContent()
        }
    }
}