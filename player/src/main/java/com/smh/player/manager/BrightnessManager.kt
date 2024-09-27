package com.smh.player.manager

import android.app.Activity
import android.provider.Settings
import android.view.WindowManager
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BrightnessManager(private val activity: Activity) {

    private var currentBrightness by mutableFloatStateOf(activity.currentBrightness)
    var currentBrightnessInt by mutableIntStateOf(0)
        private set
    var showBrightness by mutableStateOf(false)
        private set
    private var showBrightnessJob: Job? = null

    private val maxBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_FULL

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    fun adjustBrightness(ratioChange: Float) {
        scope.launch {
            toggleBrightnessVisibility()

            val updateBrightness = ratioChange * maxBrightness
            currentBrightness = (currentBrightness - updateBrightness).coerceIn(0f, maxBrightness)
            currentBrightnessInt = (currentBrightness * 15).toInt()

            withContext(Dispatchers.Main) {
                val layoutParams = activity.window.attributes
                layoutParams.screenBrightness = currentBrightness
                activity.window.attributes = layoutParams
            }
        }
    }

    private fun CoroutineScope.toggleBrightnessVisibility() {
        showBrightnessJob?.cancel()
        showBrightnessJob = launch {
            showBrightness = true
            delay(1000)
            showBrightness = false
        }
    }
}

val Activity.currentBrightness: Float
    get() = when (val brightness = window.attributes.screenBrightness) {
        in WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_OFF..WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_FULL -> brightness
        else -> Settings.System.getFloat(contentResolver, Settings.System.SCREEN_BRIGHTNESS) / 255
    }