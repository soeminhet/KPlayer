package com.smh.player.control

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.VolumeDown
import androidx.compose.material.icons.automirrored.outlined.VolumeMute
import androidx.compose.material.icons.automirrored.outlined.VolumeUp
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.smh.design.theme.KPlayerTheme

@Composable
fun VolumeControlBox(
    modifier: Modifier = Modifier,
    visible: Boolean,
    value: Int
) {
    val icon = remember(value) {
        when(value) {
            0 -> Icons.AutoMirrored.Outlined.VolumeMute
            in 1..8 -> Icons.AutoMirrored.Outlined.VolumeDown
            else -> Icons.AutoMirrored.Outlined.VolumeUp
        }
    }

    ControlBox(
        modifier = modifier,
        visible = visible,
        icon = icon,
        value = value.toString()
    )
}

@Preview
@Composable
private fun VolumeControlBoxPreview() {
    KPlayerTheme {
        Surface {
            VolumeControlBox(
                visible = true,
                value = 5
            )
        }
    }
}