package com.smh.kplayer.route

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.smh.kplayer.presentation.localVideoFolderList.LocalVideoFolderListScreen
import kotlinx.serialization.Serializable

@Serializable
object LocalVideoFolderListRoute

fun NavGraphBuilder.localVideoFolderListRoute(navController: NavController) {
    composable<LocalVideoFolderListRoute> {
        LocalVideoFolderListScreen(
            toFolderDetail = { folderName ->
                navController.navigate(LocalVideoFileListRoute(folderName = folderName))
            }
        )
    }
}