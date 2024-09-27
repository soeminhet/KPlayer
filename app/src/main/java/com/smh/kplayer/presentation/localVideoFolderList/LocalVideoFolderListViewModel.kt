package com.smh.kplayer.presentation.localVideoFolderList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smh.kplayer.repository.GlobalFileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LocalVideoFolderListViewModel @Inject constructor(
    private val globalFileRepository: GlobalFileRepository
): ViewModel() {

    private val _uiState = MutableStateFlow(LocalVideoFolderUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadFolders()
    }

    private fun loadFolders() {
        viewModelScope.launch {
            globalFileRepository.getRootVideoFolderNamesAndCount()
            globalFileRepository.rootVideoFolderNamesAndCount.collectLatest { folders ->
                _uiState.update {
                    it.copy(folders = folders)
                }
            }
        }
    }
}

data class LocalVideoFolderUiState(
    val folders: List<Pair<String, Int>> = emptyList()
)