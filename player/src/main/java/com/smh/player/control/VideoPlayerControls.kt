package com.smh.player.control

import android.content.pm.ActivityInfo
import androidx.annotation.OptIn
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.SliderDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AspectRatio
import androidx.compose.material.icons.filled.CropLandscape
import androidx.compose.material.icons.filled.FitScreen
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PictureInPictureAlt
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.ScreenRotationAlt
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.WidthWide
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.AspectRatioFrameLayout
import com.smh.design.components.KIconButton
import com.smh.design.extension.formatMSecondTime
import com.smh.design.theme.KPlayerTheme
import com.smh.design.theme.SoftBlue

@OptIn(UnstableApi::class)
@Composable
fun VideoPlayerControls(
    name: String,
    totalMs: Long,
    currentOrientation: Int,
    currentResizeMode: Int,
    currentMs: () -> Long,
    isPicSupport: Boolean,
    currentPosition: () -> Float,
    bufferedPosition: () -> Float,
    onCurrentPositionChanged: (Float) -> Unit,
    onBack: () -> Unit,
    playing: () -> Boolean,
    onPlayPause: (Boolean) -> Unit,
    onClickRotation: () -> Unit,
    onClickResize: () -> Unit,
    onClickLock: () -> Unit,
    onClickPic: () -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit
) {
    val horizontalPadding by animateDpAsState(
        targetValue = if (currentOrientation == ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE) 40.dp else 16.dp,
        label = "horizontalPadding"
    )

    val resizeIcon = remember(currentResizeMode) {
        when(currentResizeMode) {
            AspectRatioFrameLayout.RESIZE_MODE_FIT -> Icons.Default.FitScreen
            AspectRatioFrameLayout.RESIZE_MODE_FILL -> Icons.Default.AspectRatio
            AspectRatioFrameLayout.RESIZE_MODE_ZOOM -> Icons.Default.CropLandscape
            else -> Icons.Default.WidthWide
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.3f))
            .padding(horizontal = horizontalPadding)
    ) {
        TopBar(name = name, onBack = onBack)
        CenterControls(
            playing = playing,
            onPlayPause = onPlayPause,
            onPrevious = onPrevious,
            onNext = onNext
        )
        BottomControls(
            totalMs = totalMs,
            currentMs = currentMs,
            currentPosition = currentPosition,
            bufferedPosition = bufferedPosition,
            onCurrentPositionChanged = onCurrentPositionChanged,
            isPicSupport = isPicSupport,
            resizeIcon = resizeIcon,
            onClickResize = onClickResize,
            onClickRotation = onClickRotation,
            onClickLock = onClickLock,
            onClickPic = onClickPic,
        )
    }
}

@Composable
private fun BoxScope.TopBar(name: String, onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .align(Alignment.TopCenter)
            .fillMaxWidth()
            .padding(top = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            KIconButton(
                icon = Icons.AutoMirrored.Default.ArrowBack,
                onClick = onBack
            )

            Text(
                text = name,
                color = Color.White,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun BoxScope.CenterControls(
    playing: () -> Boolean,
    onPlayPause: (Boolean) -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit
) {
    val haptic = LocalHapticFeedback.current

    Row(
        modifier = Modifier.align(Alignment.Center),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(28.dp)
    ) {
        KIconButton(
            icon = Icons.Default.SkipPrevious,
            iconSize = 40.dp,
            onClick = {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                onPrevious()
            },
        )

        KIconButton(
            icon = if (playing()) Icons.Default.Pause else Icons.Default.PlayArrow,
            iconSize = 60.dp,
            onClick = { onPlayPause(playing().not()) }
        )

        KIconButton(
            icon = Icons.Default.SkipNext,
            iconSize = 40.dp,
            onClick = {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                onNext()
            }
        )
    }
}

@Composable
private fun BoxScope.BottomControls(
    totalMs: Long,
    currentMs: () -> Long,
    currentPosition: () -> Float,
    bufferedPosition: () -> Float,
    onCurrentPositionChanged: (Float) -> Unit,
    isPicSupport: Boolean,
    resizeIcon: ImageVector,
    onClickResize: () -> Unit,
    onClickRotation: () -> Unit,
    onClickLock: () -> Unit,
    onClickPic: () -> Unit
) {
    Column(
        modifier = Modifier
            .align(Alignment.BottomCenter)
            .padding(bottom = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row {
                KIconButton(
                    icon = Icons.Default.LockOpen,
                    onClick = onClickLock
                )

                KIconButton(
                    icon = Icons.Default.ScreenRotationAlt,
                    onClick = onClickRotation
                )
            }

            Row {
                if (isPicSupport) {
                    KIconButton(
                        icon = Icons.Default.PictureInPictureAlt,
                        onClick = onClickPic,
                    )
                }

                KIconButton(
                    icon = resizeIcon,
                    onClick = onClickResize,
                    iconSize = 28.dp
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = currentMs().toInt().formatMSecondTime(),
                color = Color.White,
                style = MaterialTheme.typography.labelMedium
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 20.dp),
                contentAlignment = Alignment.Center
            ) {
                androidx.compose.material.Slider(
                    value = currentPosition(),
                    onValueChange = onCurrentPositionChanged,
                    modifier = Modifier.fillMaxWidth(),
                    colors = SliderDefaults.colors(
                        thumbColor = SoftBlue,
                        activeTrackColor = SoftBlue,
                        inactiveTrackColor = Color.White.copy(0.3f)
                    )
                )

                androidx.compose.material.LinearProgressIndicator(
                    progress = bufferedPosition(),
                    color = Color.White.copy(alpha = 0.3f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 9.dp),
                )

                androidx.compose.material.LinearProgressIndicator(
                    progress = currentPosition() + 0.013f,
                    color = SoftBlue,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 9.dp),
                )
            }

            Text(
                text = totalMs.toInt().formatMSecondTime(),
                color = Color.White,
                style = MaterialTheme.typography.labelMedium
            )
        }
    }
}

@OptIn(UnstableApi::class)
@Preview(
    device = "spec:width=411dp,height=891dp,dpi=420,isRound=false,chinSize=0dp,orientation=landscape"
)
@Composable
private fun VideoPlayerControlsPreview() {
    KPlayerTheme {
        Surface(color = Color.Black) {
            VideoPlayerControls(
                name = "Video Name",
                currentMs = { 1000L },
                totalMs = 10000L,
                currentPosition = { 0.1f },
                onCurrentPositionChanged = {},
                onBack = {},
                onClickResize = {},
                playing = { true },
                onPlayPause = {},
                onClickRotation = {},
                currentOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE,
                currentResizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT,
                onClickLock = {},
                isPicSupport = true,
                onClickPic = {},
                onNext = {},
                onPrevious = {},
                bufferedPosition = { 0.5f }
            )
        }
    }
}

@OptIn(UnstableApi::class)
@Preview()
@Composable
private fun VideoPlayerControlsPortraitPreview() {
    KPlayerTheme {
        Surface(color = Color.Black) {
            VideoPlayerControls(
                name = "Video Name",
                currentMs = { 1000L },
                totalMs = 10000L,
                currentPosition = { 0.1f },
                onCurrentPositionChanged = {},
                onBack = {},
                onClickResize = {},
                playing = { true },
                onPlayPause = {},
                onClickRotation = {},
                currentOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED,
                currentResizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT,
                onClickLock = {},
                isPicSupport = false,
                onClickPic = {},
                onNext = {},
                onPrevious = {},
                bufferedPosition = { 0.5f }
            )
        }
    }
}