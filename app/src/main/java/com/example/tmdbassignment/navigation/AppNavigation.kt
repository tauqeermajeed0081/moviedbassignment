package com.example.tmdbassignment.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.example.detail.detailScreen
import com.example.detail.detailScreenRoute
import com.example.detail.DetailScreenHelper
import com.example.home.homeScreen
import com.example.home.homeScreenRoute
import com.example.player.playerScreen
import com.example.player.playerScreenRoute
import com.example.player.PlayerScreenHelper

@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = homeScreenRoute
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        homeScreen(
            onHomeItemClick = { searchItem ->
                DetailScreenHelper.currentSearchItem = searchItem
                navController.navigate(detailScreenRoute)
            }
        )

        detailScreen(
            onBackClick = {
                navController.popBackStackOrIgnore()
            },
            onPlayClick = {
                PlayerScreenHelper.currentSearchItem = DetailScreenHelper.currentSearchItem
                navController.navigate(playerScreenRoute)
            }
        )

        playerScreen(
            onBackClick = {
                navController.popBackStackOrIgnore()
            }
        )
    }
}

fun NavController.popBackStackOrIgnore() {
    if (currentBackStackEntry?.lifecycle?.currentState == Lifecycle.State.RESUMED) {
        popBackStack()
    }
}