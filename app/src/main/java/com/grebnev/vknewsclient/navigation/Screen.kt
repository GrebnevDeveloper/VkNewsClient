package com.grebnev.vknewsclient.navigation

import android.net.Uri
import com.google.gson.Gson
import com.grebnev.vknewsclient.domain.entity.FeedPost

sealed class Screen(
    val route: String,
) {
    data object RecommendationsHome : Screen(ROUTE_RECOMMENDATIONS_HOME)

    data object SubscriptionsFeed : Screen(ROUTE_SUBSCRIPTIONS_FEED)

    data object RecommendationsFeed : Screen(ROUTE_RECOMMENDATIONS_FEED)

    data object Subscriptions : Screen(ROUTE_SUBSCRIPTIONS)

    data object Profile : Screen(ROUTE_PROFILE)

    data object RecommendationsComments : Screen(ROUTE_RECOMMENDATIONS_COMMENTS) {
        fun getRouteWithArgs(feedPost: FeedPost): String {
            val feedPostJson = Uri.encode(Gson().toJson(feedPost))
            return "$ROUTE_RECOMMENDATIONS_COMMENTS_FOR_ARGS/$feedPostJson"
        }
    }

    data object SubscriptionsComments : Screen(ROUTE_SUBSCRIPTIONS_COMMENTS) {
        fun getRouteWithArgs(feedPost: FeedPost): String {
            val feedPostJson = Uri.encode(Gson().toJson(feedPost))
            return "$ROUTE_SUBSCRIPTIONS_COMMENTS_FOR_ARGS/$feedPostJson"
        }
    }

    companion object {
        const val KEY_FEED_POST = "feed_post"

        const val ROUTE_RECOMMENDATIONS_HOME = "recommendations_home"
        const val ROUTE_SUBSCRIPTIONS_FEED = "subscriptions_feed"
        const val ROUTE_RECOMMENDATIONS_COMMENTS_FOR_ARGS = "recommendations_comments"
        const val ROUTE_SUBSCRIPTIONS_COMMENTS_FOR_ARGS = "subscriptions_comments"
        const val ROUTE_RECOMMENDATIONS_COMMENTS = "$ROUTE_RECOMMENDATIONS_COMMENTS_FOR_ARGS/{$KEY_FEED_POST}"
        const val ROUTE_SUBSCRIPTIONS_COMMENTS = "$ROUTE_SUBSCRIPTIONS_COMMENTS_FOR_ARGS/{$KEY_FEED_POST}"
        const val ROUTE_RECOMMENDATIONS_FEED = "recommendations_feed"
        const val ROUTE_SUBSCRIPTIONS = "subscriptions"
        const val ROUTE_PROFILE = "profile"
    }
}