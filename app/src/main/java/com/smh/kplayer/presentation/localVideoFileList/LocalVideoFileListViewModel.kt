package com.smh.kplayer.presentation.localVideoFileList

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.smh.kplayer.data.HARDWARE_ACCELERATION
import com.smh.kplayer.data.MemoryCacheDataSource
import com.smh.player.manager.FFMPEGManager
import com.smh.player.manager.VideoInfoModel
import com.smh.kplayer.repository.GlobalFileRepository
import com.smh.kplayer.route.LocalVideoFileListRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LocalVideoFileListViewModel @Inject constructor(
    private val globalFileRepository: GlobalFileRepository,
    private val ffmpegManager: FFMPEGManager,
    private val memoryCacheDataSource: MemoryCacheDataSource,
    savedStateHandle: SavedStateHandle
): ViewModel() {
    private val route = savedStateHandle.toRoute<LocalVideoFileListRoute>()

    private val _uiState = MutableStateFlow(LocalVideoFileUiState())
    val uiState = _uiState.asStateFlow()

    init {
        updateFolderName()
        getVideoFiles()
    }

    private fun updateFolderName() {
        _uiState.update {
            it.copy(
                folderName = route.folderName
            )
        }
    }

    private fun getVideoFiles() {
        viewModelScope.launch(Dispatchers.IO) {
            val files = globalFileRepository.getVideosInFolder(route.folderName)
            _uiState.update {
                it.copy(
                    videoFiles = files
                )
            }

            val isHardwareDecodeEnabled = memoryCacheDataSource.get(HARDWARE_ACCELERATION) ?: false
            files.map { file ->
                async {
                    val thumbnail = ffmpegManager.checkCacheAndExtractFFMPEG(file.uri, isHardwareDecodeEnabled)
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
}

data class LocalVideoFileUiState(
    val folderName: String = "",
    val videoFiles: List<VideoInfoModel> = emptyList()
)