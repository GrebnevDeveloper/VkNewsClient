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
    commentsScreenContent: @Composable (FeedPost) -> Unit,
    subscriptionsFeedScreenContent: @Composable () -> Unit,
    profileScreenContent: @Composable () -> Unit,
) {
    NavHost(
        navController = navHostController,
        startDestination = Screen.RecommendationsHome.route,
    ) {
        recommendationsScreenNavGraph(
            recommendationsFeedScreenContent = recommendationsFeedScreenContent,
            commentsScreenContent = commentsScreenContent,
        )
        subscriptionsScreenNavGraph(
            subscriptionsFeedScreenContent = subscriptionsFeedScreenContent,
            commentsScreenContent = commentsScreenContent,
        )
        composable(Screen.Profile.route) {
            profileScreenContent()
        }
    }
}