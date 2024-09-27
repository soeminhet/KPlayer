package com.smh.kplayer.presentation.localVideoPlayer

import android.net.Uri
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.smh.player.VideoPlayer

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun LocalVideoPlayerScreen(
    uri: Uri,
    onBack: () -> Unit,
    viewModel: LocalVideoPlayerViewModel = hiltViewModel()
) {
    val videos by viewModel.videos.collectAsState()
    val currentIndex = remember(videos) { mutableIntStateOf(videos.indexOfFirst { it.second == uri }) }
    val lastVideoIndex = remember(videos) { videos.lastIndex }

    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        if (currentIndex.intValue != -1) {
            VideoPlayer(
                uri = videos[currentIndex.intValue].second,
                name = videos[currentIndex.intValue].first,
                onBack = onBack,
                onNext = {
                    if (currentIndex.intValue < lastVideoIndex) {
                        currentIndex.intValue++
                    }
                },
                onPrevious = {
                    if (currentIndex.intValue > 0) {
                        currentIndex.intValue--
                    }
                },
            )
        }
    }
}
