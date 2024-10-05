package com.smh.kplayer.presentation.localVideoFileList

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
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
        toLocalVideoPlayer = toLocalVideoPlayer,
        onForceRefresh = viewModel::forceRefresh
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
private fun LocalVideoFileListContent(
    uiState: LocalVideoFileUiState,
    onBack: () -> Unit,
    toLocalVideoPlayer: (VideoInfoModel) -> Unit,
    onForceRefresh: () -> Unit
) {
    val pullRefreshState = rememberPullRefreshState(
        refreshing = uiState.isRefreshing,
        onRefresh = onForceRefresh
    )

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
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .pullRefresh(pullRefreshState)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
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

            PullRefreshIndicator(
                refreshing = uiState.isRefreshing,
                state = pullRefreshState,
                modifier = Modifier.align(Alignment.TopCenter),
            )
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
            toLocalVideoPlayer = {},
            onForceRefresh = {}
        )
    }
}

