package com.grebnev.vknewsclient.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.google.gson.Gson
import com.grebnev.vknewsclient.domain.entity.FeedPost

fun NavGraphBuilder.recommendationsScreenNavGraph(
    recommendationsFeedScreenContent: @Composable () -> Unit,
    commentsScreenContent: @Composable (FeedPost) -> Unit
) {
    navigation(
        startDestination = Screen.RecommendationsFeed.route,
        route = Screen.RecommendationsHome.route
    ) {
        composable(Screen.RecommendationsFeed.route) {
            recommendationsFeedScreenContent()
        }
        composable(
            route = Screen.Comments.route,
            arguments = listOf(navArgument(Screen.KEY_FEED_POST) { type = NavType.StringType })
        ) {
            val feedPostJson = it.arguments?.getString(Screen.KEY_FEED_POST) ?: ""
            val feedPost = Gson().fromJson(feedPostJson, FeedPost::class.java)
            commentsScreenContent(feedPost)
        }
    }
}

fun NavGraphBuilder.subscriptionsScreenNavGraph(
    subscriptionsFeedScreenContent: @Composable () -> Unit,
    commentsScreenContent: @Composable (FeedPost) -> Unit
) {
    navigation(
        startDestination = Screen.Subscriptions.route,
        route = Screen.SubscriptionsFeed.route
    ) {
        composable(Screen.Subscriptions.route) {
            subscriptionsFeedScreenContent()
        }
        composable(
            route = Screen.Comments.route,
            arguments = listOf(navArgument(Screen.KEY_FEED_POST) { type = NavType.StringType })
        ) {
            val feedPostJson = it.arguments?.getString(Screen.KEY_FEED_POST) ?: ""
            val feedPost = Gson().fromJson(feedPostJson, FeedPost::class.java)
            commentsScreenContent(feedPost)
        }
    }
}