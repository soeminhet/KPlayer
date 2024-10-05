package com.smh.design.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.smh.design.theme.BlueAdaptive
import com.smh.design.theme.KPlayerTheme

@Composable
fun NetworkVideoLinkDialog(
    modifier: Modifier = Modifier,
    show: Boolean,
    onClickPlay: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    if (show) {
        KDialog(
            onDismissRequest = onDismiss,
            modifier = modifier,
            content = {
                NetworkVideoLinkDialogContent(
                    onClickPlay = onClickPlay,
                    onClickCancel = onDismiss
                )
            }
        )
    }
}

@Composable
fun NetworkVideoLinkDialogContent(
    onClickPlay: (String) -> Unit,
    onClickCancel: () -> Unit
) {
    var videoLink by remember { mutableStateOf("") }
    val enablePlayButton by remember { derivedStateOf { videoLink.isNotEmpty() } }

    Column {
        Text(
            text = "Streaming",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 20.dp)
        )

        OutlinedTextField(
            value = videoLink,
            onValueChange = { videoLink = it },
            label = {
                Text(
                    text = "Enter video link",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            textStyle = MaterialTheme.typography.bodyMedium,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.onPrimary,
                focusedTextColor = MaterialTheme.colorScheme.onPrimary,
                focusedLabelColor = MaterialTheme.colorScheme.onPrimary,
                cursorColor = MaterialTheme.colorScheme.onPrimary
            )
        )

        Button(
            onClick = { onClickPlay(videoLink) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = BlueAdaptive,
                contentColor = Color.White
            ),
            enabled = enablePlayButton,
        ) {
            Text("PLAY")
        }

        Button(
            onClick = onClickCancel,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text("CANCEL")
        }
    }
}


@Preview
@Composable
private fun NetworkVideoLinkDialogPreview() {
    KPlayerTheme {
        Surface {
            NetworkVideoLinkDialog(
                show = true,
                onDismiss = {},
                onClickPlay = {},
            )
        }
    }
}