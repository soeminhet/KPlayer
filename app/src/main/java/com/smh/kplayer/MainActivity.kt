package com.smh.kplayer

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.smh.design.extension.setFullScreen
import com.smh.kplayer.route.LocalVideoFolderListRoute
import com.smh.kplayer.route.LocalVideoPlayerRoute
import com.smh.kplayer.route.localVideoFileListRoute
import com.smh.kplayer.route.localVideoFolderListRoute
import com.smh.kplayer.route.localVideoPlayerRoute
import com.smh.design.theme.KPlayerTheme
import com.smh.design.util.PermissionUtils
import com.smh.kplayer.util.slideNavTransition
import dagger.hilt.android.AndroidEntryPoint
import kotlin.reflect.KClass

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    private val requestPermissionsLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.all { it.value }) {
            viewModel.loadInitialData()
        } else {
            Log.i("PERMISSIONS", "Permissions denied")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        splashScreen.setKeepOnScreenCondition { !viewModel.initialLoadingFinished.value }

        setContent {
            val navController = rememberNavController()
            val backStackEntry by navController.currentBackStackEntryAsState()
            val isHideStatusBar by remember(backStackEntry) {
                derivedStateOf {
                    backStackEntry.hasAnyRoute(
                        LocalVideoPlayerRoute::class
                    )
                }
            }

            LaunchedEffect(isHideStatusBar) {
                setFullScreen(isHideStatusBar)
            }

            KPlayerTheme {
                Surface {
                    NavHost(
                        navController = navController,
                        startDestination = LocalVideoFolderListRoute,
                        enterTransition = { slideNavTransition.enterTransition },
                        exitTransition = { slideNavTransition.exitTransition },
                        popEnterTransition = { slideNavTransition.popEnterTransition },
                        popExitTransition = { slideNavTransition.popExitTransition },
                    ) {
                        localVideoFolderListRoute(navController)
                        localVideoFileListRoute(navController)
                        localVideoPlayerRoute(navController)
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (!PermissionUtils.hasPermissions(this)) {
            viewModel.skipInitialLoading()
            requestPermissionsLauncher.launch(PermissionUtils.REQUIRED_PERMISSIONS)
        } else {
            viewModel.loadInitialData()
        }
    }
}

private fun NavBackStackEntry?.hasAnyRoute(vararg routes: KClass<*>): Boolean {
    return routes.any { this?.destination?.hasRoute(it) == true }
}