package com.smh.kplayer.util

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally

const val NavAnimationFast = 300

data class NavTransition(
    val enterTransition: EnterTransition,
    val exitTransition: ExitTransition,
    val popEnterTransition: EnterTransition,
    val popExitTransition: ExitTransition
)

val slideNavTransition = NavTransition(
    enterTransition = slideInHorizontally(
        initialOffsetX = { 1500 },
        animationSpec = tween(NavAnimationFast)
    ),
    exitTransition = slideOutHorizontally(
        targetOffsetX = { -1500 },
        animationSpec = tween(NavAnimationFast)
    ),
    popEnterTransition = slideInHorizontally(
        initialOffsetX = { -1500 },
        animationSpec = tween(NavAnimationFast)
    ),
    popExitTransition = slideOutHorizontally(
        targetOffsetX = { 1500 },
        animationSpec = tween(NavAnimationFast)
    )
)