package com.grebnev.vknewsclient

import androidx.compose.foundation.clickable
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.currentBackStackEntryAsState
import com.grebnev.vknewsclient.navigation.AppNavGraph
import com.grebnev.vknewsclient.navigation.NavigationState
import com.grebnev.vknewsclient.navigation.rememberNavigationState


@Composable
fun VkNewsMainScreen(
    viewModel: VkNewsMainScreenViewModel
) {
    val navigationState = rememberNavigationState()

    Scaffold(
        topBar = { TopBar() },
        bottomBar = { NavigationBottomBar(navigationState) },
        content = { paddingValues ->
            AppNavGraph(
                navHostController = navigationState.navHostController,
                homeScreenContent = {
                    HomeScreen(viewModel = viewModel, paddingValues = paddingValues)
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

    Text(modifier = Modifier.clickable { count++ },
        text = "$name count: $count"
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
private fun NavigationBottomBar(
    navigationState: NavigationState
    ) {

    val navBackStackEntry by navigationState.navHostController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar {
        val items = listOf(
            NavigationItem.Home,
            NavigationItem.Favourite,
            NavigationItem.Profile
        )
        items.forEach() { item ->
            NavigationBarItem(
                selected = currentRoute == item.screen.route,
                onClick = { navigationState.navigateTo(item.screen.route) },
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