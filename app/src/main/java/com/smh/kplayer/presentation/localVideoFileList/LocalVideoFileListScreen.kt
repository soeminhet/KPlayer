package com.smh.kplayer.presentation.localVideoFileList

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.smh.player.manager.VideoInfoModel
import com.smh.design.components.VideoFileItem
import com.smh.design.theme.KPlayerTheme

@Composable
fun LocalVideoFileListScreen(
    onBack: () -> Unit,
    toLocalVideoPlayer: (VideoInfoModel) -> Unit,
    viewModel: LocalVideoFileListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LocalVideoFileListContent(
        uiState = uiState,
        onBack = onBack,
        toLocalVideoPlayer = toLocalVideoPlayer
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LocalVideoFileListContent(
    uiState: LocalVideoFileUiState,
    onBack: () -> Unit,
    toLocalVideoPlayer: (VideoInfoModel) -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = uiState.folderName) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
        ) {
            items(
                uiState.videoFiles,
                key = { file -> file.uri }
            ) { file ->
                VideoFileItem(
                    image = file.thumbnail,
                    folderName = file.name,
                    size = file.size,
                    onClick = { toLocalVideoPlayer(file) }
                )
            }
        }
    }
}

@Preview
@Composable
private fun LocalVideoFileListPreview() {
    KPlayerTheme {
        LocalVideoFileListContent(
            uiState = LocalVideoFileUiState(
                folderName = "Folder Name",
                videoFiles = VideoInfoModel.examples
            ),
            onBack = {},
            toLocalVideoPlayer = {}
        )
    }
}

