package com.smh.player.control

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FastForward
import androidx.compose.material.icons.outlined.FastRewind
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.smh.design.theme.KPlayerTheme

@Composable
fun FastSeekControlBox(
    modifier: Modifier = Modifier,
    seekForward: Boolean,
    seekBackward: Boolean,
    seekValue: Int
) {
    FastSeekForwardControlBox(modifier = modifier, visible = seekForward, value = seekValue)
    FastSeekBackwardControlBox(modifier = modifier, visible = seekBackward, value = -seekValue)
}

@Composable
fun FastSeekForwardControlBox(
    modifier: Modifier = Modifier,
    visible: Boolean,
    value: Int
) {
    ControlBox(
        modifier = modifier,
        visible = visible,
        icon = Icons.Outlined.FastForward,
        value = "+$value",
    )
}

@Composable
fun FastSeekBackwardControlBox(
    modifier: Modifier = Modifier,
    visible: Boolean,
    value: Int
) {
    ControlBox(
        modifier = modifier,
        visible = visible,
        icon = Icons.Outlined.FastRewind,
        value = "$value",
    )
}

@Preview
@Composable
private fun FastSeekControlBoxPreview() {
    KPlayerTheme {
        Surface {
            FastSeekControlBox(
                seekForward = true,
                seekBackward = false,
                seekValue = 10
            )
        }
    }
}