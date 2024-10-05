package com.smh.player

import android.content.Context
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.net.Uri
import android.util.Log
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.FrameLayout
import androidx.activity.compose.BackHandler
import androidx.annotation.OptIn
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.VideoSize
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.DefaultRenderersFactory.EXTENSION_RENDERER_MODE_ON
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.dash.DashMediaSource
import androidx.media3.exoplayer.hls.HlsMediaSource
import androidx.media3.exoplayer.smoothstreaming.SsMediaSource
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.exoplayer.source.MediaSource
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import com.smh.design.components.KAlertDialog
import com.smh.design.components.KIconButton
import com.smh.design.extension.findActivity
import com.smh.design.extension.formatMSecondTime
import com.smh.player.control.BrightnessControlBox
import com.smh.player.control.FastSeekControlBox
import com.smh.player.control.VideoPlayerControls
import com.smh.player.control.VolumeControlBox
import com.smh.player.manager.BrightnessManager
import com.smh.player.manager.CacheManager
import com.smh.player.manager.PictureInPictureManager
import com.smh.player.manager.VolumeManager
import io.github.anilbeesetti.nextlib.media3ext.ffdecoder.NextRenderersFactory
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.roundToInt

@ExperimentalComposeUiApi
@OptIn(UnstableApi::class)
@Composable
fun VideoPlayer(
    modifier: Modifier = Modifier,
    name: String,
    uri: Uri,
    onBack: () -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val activity = remember { context.findActivity() }
    val scope = rememberCoroutineScope()

    val volumeManager = remember { VolumeManager(context) }
    val brightnessManager = remember { BrightnessManager(activity) }
    val pipManager = remember { PictureInPictureManager(context) }
    val cacheManager = remember { CacheManager }

    var totalDuration by remember { mutableLongStateOf(0L) }
    var currentDuration by rememberSaveable { mutableLongStateOf(0L) }
    var currentProgress by remember { mutableFloatStateOf(0f) }
    var bufferedProgress by remember { mutableFloatStateOf(0f) }
    var videoSize by remember { mutableStateOf(VideoSize.UNKNOWN) }
    var infoText by remember { mutableStateOf("") }
    var isPlaying by remember { mutableStateOf(false) }
    var orientation by remember { mutableIntStateOf(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE) }
    var currentResizeMode by remember { mutableIntStateOf(AspectRatioFrameLayout.RESIZE_MODE_FIT) }

    var seekForwardJob: Job? = null
    var seekBackwardJob: Job? = null
    var showControllerJob: Job? = null
    var resetSeekAmountJob: Job? = null
    var infoTextJob: Job? = null

    var seekAmount by remember { mutableFloatStateOf(0f) }
    var videoScale by remember { mutableFloatStateOf(0f) }
    var isSeekingForward by remember { mutableStateOf(false) }
    var isSeekingBackward by remember { mutableStateOf(false) }
    var showController by remember { mutableStateOf(false) }
    var isLocked by remember { mutableStateOf(false) }
    var showUnsupportedFormatDialog by remember { mutableStateOf(false) }

    val loadControl = DefaultLoadControl.Builder()
        .setBufferDurationsMs(50000, 6000000, 5000, 5000)
        .build()

    val exoPlayer = remember {
        ExoPlayer.Builder(context)
            .setRenderersFactory(
                NextRenderersFactory(context)
                    .setEnableDecoderFallback(true)
                    .setExtensionRendererMode(EXTENSION_RENDERER_MODE_ON)
            )
            .setTrackSelector(DefaultTrackSelector(context))
            .setLoadControl(loadControl)
            .setSeekBackIncrementMs(10000)
            .setSeekBackIncrementMs(10000)
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(C.AUDIO_CONTENT_TYPE_MOVIE)
                    .setUsage(C.USAGE_MEDIA)
                    .build(),
                true,
            )
            .apply {
                val cache = cacheManager.getCache(context)
                val cacheDataSourceFactory = CacheDataSource.Factory()
                    .setCache(cache)
                    .setUpstreamDataSourceFactory(DefaultDataSource.Factory(context))
                setMediaSourceFactory(DefaultMediaSourceFactory(cacheDataSourceFactory))
            }
            .build()
            .apply {
                addListener(object : Player.Listener {
                    override fun onPlayerError(error: PlaybackException) {
                        super.onPlayerError(error)
                        Log.e("EXO_PLAYER", error.message.toString())
                        showUnsupportedFormatDialog = true
                    }

                    override fun onPlaybackStateChanged(playbackState: Int) {
                        super.onPlaybackStateChanged(playbackState)
                        if (playbackState == Player.STATE_READY) {
                            showUnsupportedFormatDialog = false
                            totalDuration = duration
                            volume = 1f
                            volumeManager.setLoudnessEnhancer(audioSessionId)
                        }
                    }

                    override fun onIsPlayingChanged(isPlayingIntenal: Boolean) {
                        super.onIsPlayingChanged(isPlaying)
                        isPlaying = isPlayingIntenal
                    }

                    override fun onVideoSizeChanged(videoSizeInternal: VideoSize) {
                        super.onVideoSizeChanged(videoSize)
                        Log.i("ZOOM_VIDEO_SIZE", videoSize.width.toString())
                        videoSize = videoSizeInternal
                    }

                    override fun onIsLoadingChanged(isLoading: Boolean) {
                        super.onIsLoadingChanged(isLoading)
                        Log.i("EXO_PLAYER", "Loading: $isLoading")
                    }
                })
            }
    }

    val playerView = remember {
        PlayerView(context).apply {
            hideController()
            useController = false
            resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
            currentResizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT

            player = exoPlayer
            layoutParams = FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
        }
    }

    LaunchedEffect(uri, exoPlayer) {
        val mediaSource = generateMediaSourceFactory(context, uri)
        exoPlayer.setMediaSource(mediaSource)
        exoPlayer.prepare()
        exoPlayer.playWhenReady = true
    }

    LaunchedEffect(isPlaying) {
        while (isPlaying) {
            currentDuration = exoPlayer.currentPosition
            if (currentDuration > 0 && totalDuration > 0) {
                currentProgress = (currentDuration / 1000f) / (totalDuration / 1000f)
                bufferedProgress  = ((currentDuration + exoPlayer.totalBufferedDuration) / 1000f) / (totalDuration / 1000f)
            }
            delay(1000L)
        }
    }

    fun setInfoText(value: String) {
        infoTextJob?.cancel()
        infoTextJob = scope.launch {
            infoText = value
            delay(1000)
            infoText = ""
        }
    }

    BackHandler {
        if (!isLocked) {
            onBack()
        }
    }

    KAlertDialog(
        show = showUnsupportedFormatDialog,
        title = "Sorry!",
        text = "This video format is not supported.",
        onDismiss = {
            showUnsupportedFormatDialog = false
        }
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .pointerInput(isLocked) {
                if (!isLocked) {
                    detectTransformGestures { centroid, pan, zoom, _ ->
                        if (abs(pan.x) < 10 && abs(pan.y) < 10) {
                            Log.i("ZOOM", zoom.toString())
                            updateVideoScale(
                                playerView = playerView,
                                videoSize = videoSize,
                                zoom = zoom,
                                setInfoText = ::setInfoText,
                                currentVideoScale = videoScale,
                                onCurrentVideoScaleChanged = { videoScale = it }
                            )
                        } else if (abs(pan.y) < 10) {
                            resetSeekAmountJob?.cancel()
                            resetSeekAmountJob = scope.launch {
                                delay(1500)
                                seekAmount = 0f
                            }
                            handlePanGesture(
                                pan = pan,
                                width = size.width,
                                totalDuration = totalDuration,
                                currentProgress = currentProgress,
                                exoPlayer = exoPlayer,
                                setInfoText = ::setInfoText,
                                seekAmount = seekAmount,
                                onSeekAmountChanged = { seekAmount = it },
                                onCurrentProgressChanged = { currentProgress = it }
                            )
                        } else {
                            val (distanceX, distanceY) = pan
                            val viewCenterX = playerView.measuredWidth / 2
                            val distanceFull = playerView.measuredHeight * 0.66f
                            val ratioChange = distanceY / distanceFull

                            handlePanGesture(
                                distanceX = distanceX,
                                distanceY = distanceY,
                                centroidX = centroid.x,
                                viewCenterX = viewCenterX,
                                ratioChange = ratioChange,
                                volumeManager = volumeManager,
                                brightnessManager = brightnessManager
                            )
                        }
                    }
                }
            }
            .pointerInput(isLocked) {
                if (!isLocked) {
                    detectTapGestures(
                        onTap = {
                            showControllerJob?.cancel()
                            showControllerJob = scope.launch {
                                showController = true
                                delay(5000)
                                showController = false
                            }
                        },
                        onDoubleTap = { offset ->
                            val eventPositionX = offset.x / playerView.measuredWidth
                            if (eventPositionX < 0.35) {
                                seekBackwardJob?.cancel()
                                seekBackwardJob = scope.launch {
                                    isSeekingBackward = true
                                    delay(1000)
                                    isSeekingBackward = false
                                }
                                exoPlayer.seekBack()
                            } else if (eventPositionX > 0.65) {
                                seekForwardJob?.cancel()
                                seekForwardJob = scope.launch {
                                    isSeekingForward = true
                                    delay(1000)
                                    isSeekingForward = false
                                }
                                exoPlayer.seekForward()
                            } else {
                                if (isPlaying) exoPlayer.pause() else exoPlayer.play()
                            }
                        },
                    )
                }
            }
    ) {
        AndroidView(
            modifier = Modifier
                .fillMaxSize(),
            factory = { playerView },
        )

        if (isLocked) {
            KIconButton(
                icon = Icons.Default.Lock,
                onClick = {
                    isLocked = false
                    showControllerJob?.cancel()
                    showControllerJob = scope.launch {
                        showController = true
                        delay(3000)
                        showController = false
                    }
                },
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(24.dp)
            )
        }

        AnimatedVisibility(
            visible = showController,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.fillMaxSize()
        ) {
            VideoPlayerControls(
                name = name,
                currentMs = { currentDuration },
                totalMs = totalDuration,
                currentPosition = { currentProgress },
                bufferedPosition = { bufferedProgress },
                onCurrentPositionChanged = { value ->
                    currentProgress = value
                    val seekPosition = totalDuration * value
                    exoPlayer.seekTo(seekPosition.toLong())
                },
                onBack = onBack,
                onClickResize = {
                    val currentMode = playerView.resizeMode
                    when (currentMode) {
                        AspectRatioFrameLayout.RESIZE_MODE_FIT -> {
                            playerView.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FILL
                            currentResizeMode = AspectRatioFrameLayout.RESIZE_MODE_FILL
                            setInfoText("FILL")
                        }
                        AspectRatioFrameLayout.RESIZE_MODE_FILL -> {
                            playerView.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                            currentResizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                            setInfoText("ZOOM")
                        }
                        AspectRatioFrameLayout.RESIZE_MODE_ZOOM -> {
                            playerView.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIXED_WIDTH
                            currentResizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIXED_WIDTH
                            setInfoText("STRETCH")
                        }
                        else -> {
                            playerView.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
                            currentResizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
                            setInfoText("FIT")
                        }
                    }
                },
                playing = { isPlaying },
                onPlayPause = {
                    if (it) exoPlayer.play() else exoPlayer.pause()
                },
                onClickRotation = {
                    orientation = when (context.resources.configuration.orientation) {
                        Configuration.ORIENTATION_LANDSCAPE -> ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT
                        else -> ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
                    }
                    activity.requestedOrientation = orientation
                },
                currentOrientation = orientation,
                currentResizeMode = currentResizeMode,
                onClickLock = {
                    isLocked = !isLocked
                    showControllerJob?.cancel()
                    showController = false
                },
                isPicSupport = pipManager.isPipSupported,
                onClickPic = {
                    showController = false
                    pipManager.enterPIPMode(playerView)
                },
                onNext = onNext,
                onPrevious = onPrevious
            )
        }

        Text(
            text = infoText,
            color = Color.White,
            style = MaterialTheme.typography.displaySmall,
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = (-80).dp)
        )

        FastSeekControlBox(
            seekForward = isSeekingForward,
            seekBackward = isSeekingBackward,
            seekValue = 10,
            modifier = Modifier.align(alignment = Alignment.Center)
        )

        BrightnessControlBox(
            visible = brightnessManager.showBrightness,
            value = brightnessManager.currentBrightnessInt,
            modifier = Modifier.align(alignment = Alignment.Center)
        )

        VolumeControlBox(
            visible = volumeManager.showVolume,
            value = volumeManager.currentVolume.toInt(),
            modifier = Modifier.align(alignment = Alignment.Center)
        )
    }

    DisposableEffect(Unit) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> {
                    activity.requestedOrientation = orientation
                }

                Lifecycle.Event.ON_PAUSE -> {
                    if (!pipManager.isActivityStatePipMode()) {
                        exoPlayer.pause()
                    }
                }

                Lifecycle.Event.ON_STOP -> {
                    activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
                }

                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            cacheManager.releaseCache()
            lifecycleOwner.lifecycle.removeObserver(observer)
            exoPlayer.release()
        }
    }
}

private fun updateVideoScale(
    playerView: PlayerView,
    videoSize: VideoSize,
    zoom: Float,
    currentVideoScale: Float,
    onCurrentVideoScaleChanged: (Float) -> Unit,
    setInfoText: (String) -> Unit,
) {
    val playerViewWidth = playerView.width
    val playerViewScaleX = playerView.scaleX
    val videoSizeWidth = videoSize.width.toFloat()

    val scaleFactor = playerViewScaleX * zoom
    val updatedVideoScale = (playerViewWidth * scaleFactor) / videoSizeWidth

    if (currentVideoScale != updatedVideoScale && updatedVideoScale in 0.25f..4.0f) {
        if (currentVideoScale == 0f) {
            onCurrentVideoScaleChanged(updatedVideoScale)
        } else {
            onCurrentVideoScaleChanged(updatedVideoScale)
            setInfoText((updatedVideoScale * 100).roundToInt().toString() + "%")
        }
        playerView.scaleX = scaleFactor
        playerView.scaleY = scaleFactor
    }
}

private fun handlePanGesture(
    pan: Offset,
    width: Int,
    totalDuration: Long,
    currentProgress: Float,
    exoPlayer: ExoPlayer,
    seekAmount: Float,
    setInfoText: (String) -> Unit,
    onSeekAmountChanged: (Float) -> Unit,
    onCurrentProgressChanged: (Float) -> Unit
) {
    val currentSeekAmount = (pan.x / width) * 0.1f
    val updatedSeekAmount = seekAmount + currentSeekAmount

    val seekMSeconds = (totalDuration * abs(updatedSeekAmount)).toInt()
    val seekTimeText = (if (seekAmount < 0) "-" else "+") + seekMSeconds.formatMSecondTime()
    setInfoText(seekTimeText)

    val seekPosition = (totalDuration * currentProgress).toLong()
    exoPlayer.seekTo(seekPosition)

    val updatedPosition = (currentProgress + currentSeekAmount).coerceIn(0f, 1f)
    onSeekAmountChanged(updatedSeekAmount)
    onCurrentProgressChanged(updatedPosition)
}

private fun handlePanGesture(
    distanceX: Float,
    distanceY: Float,
    centroidX: Float,
    viewCenterX: Int,
    ratioChange: Float,
    volumeManager: VolumeManager,
    brightnessManager: BrightnessManager
) {
    if (abs(distanceY / distanceX) >= 2) {
        if (centroidX > viewCenterX) {
            volumeManager.adjustVolume(ratioChange)
        } else {
            brightnessManager.adjustBrightness(ratioChange)
        }
    }
}

@OptIn(UnstableApi::class)
fun generateMediaSourceFactory(
    context: Context,
    videoUri: Uri
): MediaSource {
    val dataSourceFactory = if (videoUri.scheme == "http" || videoUri.scheme == "https") {
        DefaultDataSource.Factory(context, DefaultHttpDataSource.Factory().setAllowCrossProtocolRedirects(true))
    } else {
        DefaultDataSource.Factory(context)
    }
    val uriPath = videoUri.lastPathSegment ?: ""

    return when {
        uriPath.endsWith(".mpd", ignoreCase = true) -> {
            DashMediaSource.Factory(dataSourceFactory).createMediaSource(MediaItem.fromUri(videoUri))
        }
        uriPath.endsWith(".m3u8", ignoreCase = true) -> {
            HlsMediaSource.Factory(dataSourceFactory).createMediaSource(MediaItem.fromUri(videoUri))
        }
        uriPath.endsWith(".ism", ignoreCase = true) || uriPath.endsWith("/Manifest", ignoreCase = true) -> {
            SsMediaSource.Factory(dataSourceFactory).createMediaSource(MediaItem.fromUri(videoUri))
        }
        else -> {
            ProgressiveMediaSource.Factory(dataSourceFactory)
                .setContinueLoadingCheckIntervalBytes(1024 * 1024 * 100)
                .createMediaSource(MediaItem.fromUri(videoUri))
        }
    }
}

