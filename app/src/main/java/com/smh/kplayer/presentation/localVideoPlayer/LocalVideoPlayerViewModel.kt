package com.smh.kplayer.presentation.localVideoPlayer

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.smh.kplayer.repository.GlobalFileRepository
import com.smh.kplayer.route.LocalVideoPlayerRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class LocalVideoPlayerViewModel @Inject constructor(
    private val globalFileRepository: GlobalFileRepository,
    savedStateHandle: SavedStateHandle
): ViewModel() {

    private val route = savedStateHandle.toRoute<LocalVideoPlayerRoute>()

    val videos = flow {
        val files = globalFileRepository.getVideosInFolder(route.folderName)
        emit(files.map { it.name to it.uri })
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )
}