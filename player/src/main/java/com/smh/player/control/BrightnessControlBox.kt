package com.smh.player.control

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Brightness5
import androidx.compose.material.icons.filled.Brightness6
import androidx.compose.material.icons.filled.Brightness7
import androidx.compose.material.icons.outlined.Brightness4
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.smh.design.theme.KPlayerTheme

@Composable
fun BrightnessControlBox(
    modifier: Modifier = Modifier,
    visible: Boolean,
    value: Int
) {
    val icon = remember(value) {
        when(value) {
            in 0..4 -> Icons.Outlined.Brightness4
            in 5..8 -> Icons.Filled.Brightness5
            in 8..12 -> Icons.Filled.Brightness6
            else -> Icons.Filled.Brightness7
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
private fun BrightnessControlBoxPreview() {
    KPlayerTheme {
        Surface {
            BrightnessControlBox(
                visible = true,
                value = 5
            )
        }
    }
}