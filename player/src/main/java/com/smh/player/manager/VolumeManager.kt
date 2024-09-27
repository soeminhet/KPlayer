package com.smh.player.manager

import android.content.Context
import android.media.AudioManager
import android.media.audiofx.LoudnessEnhancer
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat.getSystemService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class VolumeManager(context: Context) {

    private val audioManager: AudioManager by lazy {
        getSystemService(context, AudioManager::class.java) as AudioManager
    }

    private var loudnessEnhancer: LoudnessEnhancer? = null

    var maxStreamVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        private set

    var currentVolume by mutableFloatStateOf(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC).toFloat())
        private set

    private var showVolumeJob: Job? = null

    var showVolume by mutableStateOf(false)
        private set

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    fun setLoudnessEnhancer(audioSessionId: Int) {
        if (loudnessEnhancer == null) {
            maxStreamVolume *= 2
            loudnessEnhancer = LoudnessEnhancer(audioSessionId).apply {
                enabled = true
                setTargetGain(maxStreamVolume)
            }
        }
    }

    fun adjustVolume(ratioChange: Float) {
        scope.launch {
            toggleVolumeVisibility()
            val updatedVolume = ratioChange * maxStreamVolume
            currentVolume = (currentVolume - updatedVolume).coerceIn(0f, maxStreamVolume.toFloat())
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, currentVolume.toInt(), 0)
        }
    }

    private fun CoroutineScope.toggleVolumeVisibility() {
        showVolumeJob?.cancel()
        showVolumeJob = launch {
            showVolume = true
            delay(1000)
            showVolume = false
        }
    }
}