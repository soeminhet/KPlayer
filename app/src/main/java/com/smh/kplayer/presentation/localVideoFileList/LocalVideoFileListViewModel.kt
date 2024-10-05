package com.smh.kplayer.presentation.localVideoFileList

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.toRoute
import com.smh.kplayer.repository.GlobalFileRepository
import com.smh.kplayer.route.LocalVideoFileListRoute
import com.smh.player.manager.FFMPEGManager
import com.smh.player.manager.VideoInfoModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LocalVideoFileListViewModel @Inject constructor(
    private val globalFileRepository: GlobalFileRepository,
    private val ffmpegManager: FFMPEGManager,
    savedStateHandle: SavedStateHandle
): ViewModel() {
    private val route = savedStateHandle.toRoute<LocalVideoFileListRoute>()

    private val _uiState = MutableStateFlow(LocalVideoFileUiState())
    val uiState = _uiState.asStateFlow()

    init {
        updateFolderName()
        getVideoFiles(forceRefresh = false)
    }

    private fun updateFolderName() {
        _uiState.update {
            it.copy(
                folderName = route.folderName
            )
        }
    }

    private fun getVideoFiles(forceRefresh: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            val files = globalFileRepository.getVideosInFolder(route.folderName, forceRefresh = forceRefresh)
            _uiState.update {
                it.copy(
                    videoFiles = files
                )
            }
            files.map { file ->
                async {
                    val thumbnail = ffmpegManager.checkCacheAndExtractFFMPEG(file.uri)
                    val updatedFile = file.copy(thumbnail = thumbnail)
                    _uiState.update { state ->
                        state.copy(
                            videoFiles = state.videoFiles.map { existingFile ->
                                if (existingFile.uri == updatedFile.uri) updatedFile else existingFile
                            }
                        )
                    }
                }
            }.awaitAll()
        }
    }

    fun forceRefresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true) }
            getVideoFiles(forceRefresh = true)
            delay(300)
            _uiState.update { it.copy(isRefreshing = false) }
        }
    }
}

data class LocalVideoFileUiState(
    val isRefreshing: Boolean = false,
    val folderName: String = "",
    val videoFiles: List<VideoInfoModel> = emptyList()
)