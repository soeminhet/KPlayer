package com.smh.kplayer.presentation.localVideoFolderList

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.smh.design.components.VideoFolderItem
import com.smh.design.theme.KPlayerTheme

@Composable
fun LocalVideoFolderListScreen(
    toFolderDetail: (String) -> Unit,
    viewModel: LocalVideoFolderListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LocalVideoFolderListContent(
        uiState = uiState,
        toFolderDetail = toFolderDetail
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LocalVideoFolderListContent(
    uiState: LocalVideoFolderUiState,
    toFolderDetail: (String) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Folders") },
                modifier = Modifier.statusBarsPadding()
            )
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .verticalScroll(state = rememberScrollState())
                .padding(paddingValues)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            uiState.folders.fastForEach { folder ->
                VideoFolderItem(
                    folderName = folder.first,
                    videoCount = folder.second,
                    onClick = toFolderDetail
                )
            }
        }
    }
}

@Preview
@Composable
private fun LocalVideoFolderListPreview() {
    KPlayerTheme {
        LocalVideoFolderListContent(
            uiState = LocalVideoFolderUiState(
                folders = listOf(
                    "Folder 1" to 10,
                    "Folder 2" to 5,
                    "Folder 3" to 3
                )
            ),
            toFolderDetail = {}
        )
    }
}