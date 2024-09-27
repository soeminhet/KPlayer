package com.smh.kplayer.route

import androidx.core.net.toUri
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.smh.kplayer.presentation.localVideoPlayer.LocalVideoPlayerScreen
import kotlinx.serialization.Serializable

@Serializable
data class LocalVideoPlayerRoute(
    val uri: String,
    val folderName: String
)

fun NavGraphBuilder.localVideoPlayerRoute(navController: NavController) {
    composable<LocalVideoPlayerRoute> {
        val route = it.toRoute<LocalVideoPlayerRoute>()
        LocalVideoPlayerScreen(
            uri = route.uri.toUri(),
            onBack = navController::navigateUp
        )
    }
}

