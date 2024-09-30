package com.smh.kplayer.presentation.onlineVideoPlayer

import android.net.Uri
import androidx.annotation.OptIn
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.core.net.toUri
import androidx.media3.common.util.UnstableApi
import com.smh.player.VideoPlayer

@ExperimentalComposeUiApi
@OptIn(UnstableApi::class)
@Composable
fun OnlineVideoPlayerScreen(
    url: String,
    onBack: () -> Unit,
) {
    val streamUrl = remember(url) { convertDriveUrlToStreamUrl(url) }

    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        VideoPlayer(
            name = "",
            uri = streamUrl.toUri(),
            onPrevious = {},
            onNext = {},
            onBack = onBack,
            modifier = Modifier.fillMaxSize()
        )
    }
}

private fun convertDriveUrlToStreamUrl(url: String): String {
    val uri = Uri.parse(url)

    return when {
        url.contains("drive.google.com") -> {
            val fileId = when {
                uri.pathSegments.contains("file") && uri.pathSegments.contains("d") -> {
                    uri.pathSegments[uri.pathSegments.indexOf("d") + 1]
                }
                url.contains("open?id=") -> {
                    uri.getQueryParameter("id")
                }
                else -> null
            }

            fileId?.let {
                "https://drive.google.com/uc?export=download&id=$fileId"
            } ?: url
        }
        else -> url
    }
}
