package com.example.player

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import coil.compose.AsyncImage
import com.example.core.utils.AppConstants
import com.example.model.response.SearchItem

@Composable
internal fun PlayerRoute(
    searchItem: SearchItem?,
    onBackClick: () -> Unit
) {
    PlayerScreen(
        searchItem = searchItem,
        onBackClick = onBackClick
    )
}

@Composable
fun PlayerScreen(
    searchItem: SearchItem?,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current

    // Immersive mode and orientation setup
    DisposableEffect(context) {
        val activity = context as? android.app.Activity
        val window = activity?.window
        val windowInsetsController =
            window?.let { WindowCompat.getInsetsController(it, it.decorView) }

        // Save original orientation
        val originalOrientation = activity?.requestedOrientation

        // Allow sensor orientation (both portrait and landscape)
        activity?.requestedOrientation = android.content.pm.ActivityInfo.SCREEN_ORIENTATION_SENSOR

        // Hide system bars
        windowInsetsController?.apply {
            hide(WindowInsetsCompat.Type.systemBars())
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }

        onDispose {
            // Restore original orientation when leaving
            activity?.requestedOrientation =
                originalOrientation ?: android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

            // Restore system bars when leaving
            windowInsetsController?.show(WindowInsetsCompat.Type.systemBars())
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Background Image (full screen)
        if (!searchItem?.backdropPath.isNullOrBlank()) {
            AsyncImage(
                model = "${AppConstants.ImagePathOriginal}${searchItem?.backdropPath}",
                contentDescription = searchItem?.title ?: searchItem?.name,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else if (!searchItem?.posterPath.isNullOrBlank()) {
            AsyncImage(
                model = "${AppConstants.ImagePathOriginal}${searchItem?.posterPath}",
                contentDescription = searchItem?.title ?: searchItem?.name,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            // Fallback background
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
            )
        }

        // Semi-transparent overlay for better text readability
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.3f))
        )

        // Back button - always visible
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier
                    .background(
                        color = Color.Black.copy(alpha = 0.5f),
                        shape = MaterialTheme.shapes.medium
                    )
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }
        }

        // Fallback for null searchItem
        if (searchItem == null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No content available",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White
                )
            }
        }
    }
} 