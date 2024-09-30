package com.smh.design.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val SoftBlue = Color(0xFF9eebf0)
val DeepBlue = Color(0xFF3c8897)
val DarkBlue = Color(0xFF23515c)

val BlueAdaptive: Color
    @Composable
    get() {
        return if(isSystemInDarkTheme()) DarkBlue else DeepBlue
    }