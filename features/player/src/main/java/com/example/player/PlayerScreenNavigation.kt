package com.example.player

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.model.response.SearchItem

const val playerScreenRoute = "playerScreenRoute"

// Temporary storage for SearchItem (similar to DetailScreenHelper)
object PlayerScreenHelper {
    var currentSearchItem: SearchItem? = null
}

fun NavGraphBuilder.playerScreen(
    onBackClick: () -> Unit
) {
    composable(
        route = playerScreenRoute
    ) { backStackEntry ->
        PlayerRoute(
            searchItem = PlayerScreenHelper.currentSearchItem,
            onBackClick = onBackClick
        )
    }
} 