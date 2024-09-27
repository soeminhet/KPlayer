package com.smh.kplayer.route

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.smh.kplayer.presentation.localVideoFileList.LocalVideoFileListScreen
import kotlinx.serialization.Serializable

@Serializable
data class LocalVideoFileListRoute(
    val folderName: String
)

fun NavGraphBuilder.localVideoFileListRoute(
    navController: NavController
) {
    composable<LocalVideoFileListRoute> {
        val route = it.toRoute<LocalVideoFileListRoute>()
        LocalVideoFileListScreen(
            onBack = navController::navigateUp,
            toLocalVideoPlayer = { model ->
                navController.navigate(
                    LocalVideoPlayerRoute(
                        uri = model.uri.toString(),
                        folderName = route.folderName
                    )
                )
            }
        )
    }
}