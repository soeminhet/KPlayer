package com.smh.design.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun KIconButton(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    contentDescription: String = "",
    iconTint: Color = Color.White,
    iconSize: Dp = 24.dp,
    clickEnabled: Boolean = true,
    onClick: () -> Unit
) {
    val tintColor by animateColorAsState(
        targetValue = if (clickEnabled) iconTint else Color.Gray,
        label = "iconTint"
    )

    Icon(
        imageVector = icon,
        contentDescription = contentDescription,
        tint = tintColor,
        modifier = modifier
            .padding(12.dp)
            .size(iconSize)
            .clickable(
                enabled = clickEnabled,
                onClick = onClick
            ),
    )
}