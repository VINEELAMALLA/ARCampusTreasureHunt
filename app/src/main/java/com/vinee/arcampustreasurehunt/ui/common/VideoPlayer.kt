package com.vinee.arcampustreasurehunt.ui.common

import android.net.Uri
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import java.io.File
import androidx.media3.datasource.RawResourceDataSource

@Composable
fun VideoPlayer(
    videoName: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    
    // Create ExoPlayer instance
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build()
    }
    
    // Find raw resource ID by stripping extension from filename and looking up in res/raw
    val rawResId by remember(videoName) {
        mutableStateOf(
            context.resources.getIdentifier(
                videoName.substringBeforeLast('.'),
                "raw",
                context.packageName
            )
        )
    }

    // Setup media item from res/raw if found
    LaunchedEffect(rawResId) {
        if (rawResId != 0) {
            val uri = RawResourceDataSource.buildRawResourceUri(rawResId)
            exoPlayer.setMediaItem(MediaItem.fromUri(uri))
            exoPlayer.prepare()
            exoPlayer.playWhenReady = true
        }
    }
    
    // Cleanup player when composable is disposed
    DisposableEffect(exoPlayer) {
        onDispose {
            exoPlayer.release()
        }
    }
    
    AndroidView(
        modifier = modifier,
        factory = { context ->
            PlayerView(context).apply {
                player = exoPlayer
                useController = true
                layoutParams = FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            }
        }
    )
}
