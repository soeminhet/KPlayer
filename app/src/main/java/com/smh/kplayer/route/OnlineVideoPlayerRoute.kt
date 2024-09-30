package com.smh.kplayer.route

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.smh.kplayer.presentation.onlineVideoPlayer.OnlineVideoPlayerScreen
import kotlinx.serialization.Serializable

@Serializable
data class OnlineVideoPlayerRoute(
    val url: String,
)

@OptIn(ExperimentalComposeUiApi::class)
fun NavGraphBuilder.onlineVideoPlayerRoute(navController: NavController) {
    composable<OnlineVideoPlayerRoute> {
        val route = it.toRoute<OnlineVideoPlayerRoute>()
        OnlineVideoPlayerScreen (
            url = route.url,
            onBack = navController::navigateUp
        )
    }
}