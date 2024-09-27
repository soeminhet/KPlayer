package com.smh.design.extension

import android.app.Activity
import android.view.View
import android.view.Window
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsCompat.Type
import androidx.core.view.WindowInsetsControllerCompat

fun Activity.setFullScreen(value: Boolean) {
    val windowInsetsController =
        WindowCompat.getInsetsController(window, window.decorView)
    windowInsetsController.systemBarsBehavior =
        WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    ViewCompat.setOnApplyWindowInsetsListener(window.decorView) { view, windowInsets ->
        if(value) {
            windowInsetsController.hide(Type.systemBars())
        } else {
            windowInsetsController.show(Type.systemBars())
        }
        ViewCompat.onApplyWindowInsets(view, windowInsets)
    }
}