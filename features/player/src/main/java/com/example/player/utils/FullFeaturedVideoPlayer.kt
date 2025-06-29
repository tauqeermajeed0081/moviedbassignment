package com.example.player.utils

import android.content.pm.ActivityInfo
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import androidx.activity.compose.BackHandler
import androidx.annotation.OptIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.ViewCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.player.VideoViewModel
import kotlinx.coroutines.delay

@OptIn(UnstableApi::class)
@Composable
fun FullFeaturedVideoPlayer(
    videoUrl: String,
    modifier: Modifier = Modifier,
    autoPlay: Boolean = true,
    showControls: Boolean = true,
    thumbnailUrl: String? = null,
    viewModel: VideoViewModel
) {
    val context = LocalContext.current
    var exoPlayer by remember { mutableStateOf<ExoPlayer?>(null) }
    var showThumbnail by remember { mutableStateOf(true) }
    var showError by remember { mutableStateOf(false) }
    var isFullscreen by remember { mutableStateOf(false) }
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current

    DisposableEffect(videoUrl) {
        val player = ExoPlayer.Builder(context).build().apply {
            val mediaItem = MediaItem.fromUri(videoUrl)
            setMediaItem(mediaItem)

            seekTo(viewModel.playbackPosition)
            playWhenReady = viewModel.playWhenReady
            prepare()
        }

        exoPlayer = player

        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_STOP) {
                viewModel.playbackPosition = player.currentPosition
                viewModel.playWhenReady = player.playWhenReady
                player.playWhenReady = false
            } else if (event == Lifecycle.Event.ON_START) {
                player.playWhenReady = viewModel.playWhenReady
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            viewModel.playbackPosition = player.currentPosition
            viewModel.playWhenReady = player.playWhenReady
            player.release()
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Box(
        modifier = if (isFullscreen) {
            Modifier
                .fillMaxSize()
        } else {
            modifier
        }
    ) {
        if (exoPlayer != null) {
            AndroidView(
                factory = { context ->
                    PlayerView(context).apply {
                        layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT)
                        player = exoPlayer
                        useController = showControls
                        ViewCompat.setOnApplyWindowInsetsListener(this) { _, insets ->
                            insets
                        }
                        setControllerHideOnTouch(!showControls)
                        setOnClickListener {
                            isFullscreen = !isFullscreen
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxSize()
            )
        }

        if (showThumbnail && thumbnailUrl != null) {
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(thumbnailUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .clickable {
                        showThumbnail = false
                        exoPlayer?.playWhenReady = true
                    },
                contentScale = ContentScale.Crop
            )
        }

        if (showError) {
            Text(
                text = "Error loading video",
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        if (isFullscreen) {
            BackHandler {
                isFullscreen = false
            }
        }
    }
}
