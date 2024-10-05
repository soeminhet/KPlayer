package com.smh.kplayer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smh.kplayer.repository.GlobalFileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val globalFileRepository: GlobalFileRepository,
): ViewModel() {

    private val _initialLoadingFinished: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val initialLoadingFinished: StateFlow<Boolean> = _initialLoadingFinished.asStateFlow()

    fun loadInitialData() {
        viewModelScope.launch {
            launch {
                _initialLoadingFinished.value = false
                globalFileRepository.initLoad()
                _initialLoadingFinished.value = true
            }
        }
    }

    fun skipInitialLoading() {
        _initialLoadingFinished.value = true
    }
}