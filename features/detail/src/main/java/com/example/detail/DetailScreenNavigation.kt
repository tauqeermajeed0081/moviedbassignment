package com.example.detail

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.model.response.SearchItem

const val detailScreenRoute = "detailScreenRoute"

object DetailScreenHelper {
    var currentSearchItem: SearchItem? = null
}

fun NavGraphBuilder.detailScreen(
    onBackClick: () -> Unit,
    onPlayClick: () -> Unit,
) {
    composable(
        route = detailScreenRoute
    ) { backStackEntry ->
        DetailRoute(
            searchItem = DetailScreenHelper.currentSearchItem,
            onBackClick = onBackClick,
            onPlayClick = onPlayClick
        )
    }
} 