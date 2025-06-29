package com.example.home

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.model.response.SearchItem

const val homeScreenRoute = "homeScreenRoute"

fun NavGraphBuilder.homeScreen(
    onHomeItemClick: (SearchItem) -> Unit
) {
    composable(route = homeScreenRoute) {
        HomeRoute(
            onHomeItemClick = onHomeItemClick
        )
    }
}