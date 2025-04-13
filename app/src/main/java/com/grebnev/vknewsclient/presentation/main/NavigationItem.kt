package com.grebnev.vknewsclient.presentation.main

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.ui.graphics.vector.ImageVector
import com.grebnev.vknewsclient.R
import com.grebnev.vknewsclient.navigation.Screen

sealed class NavigationItem(
    val screen: Screen,
    val titleResId: Int,
    val icon: ImageVector,
) {
    data object Home : NavigationItem(
        screen = Screen.RecommendationsHome,
        titleResId = R.string.navigation_item_recommendations,
        icon = Icons.Outlined.Home,
    )

    data object Favourite : NavigationItem(
        screen = Screen.SubscriptionsFeed,
        titleResId = R.string.navigation_item_subscriptions,
        icon = Icons.Outlined.Favorite,
    )

    data object Profile : NavigationItem(
        screen = Screen.Profile,
        titleResId = R.string.navigation_item_profile,
        icon = Icons.Outlined.Person,
    )
}