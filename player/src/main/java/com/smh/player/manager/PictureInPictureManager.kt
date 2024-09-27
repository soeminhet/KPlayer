package com.smh.player.manager

import android.app.PictureInPictureParams
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Rational
import androidx.media3.ui.PlayerView
import com.smh.design.extension.findActivity

class PictureInPictureManager(
    private val context: Context
) {
    fun enterPIPMode(defaultPlayerView: PlayerView) {
        if (context.packageManager.hasSystemFeature(PackageManager.FEATURE_PICTURE_IN_PICTURE)) {
            defaultPlayerView.useController = false
            val params = PictureInPictureParams.Builder()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                params
                    .setTitle("KPlayer")
                    .setAspectRatio(Rational(16, 9))
                    .setSeamlessResizeEnabled(true)
            }
            context.findActivity().enterPictureInPictureMode(params.build())
        }
    }

    fun isActivityStatePipMode(): Boolean {
        val currentActivity = context.findActivity()
        return currentActivity.isInPictureInPictureMode
    }

    val isPipSupported: Boolean by lazy {
        context.packageManager.hasSystemFeature(PackageManager.FEATURE_PICTURE_IN_PICTURE)
    }
}