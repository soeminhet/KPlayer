package com.smh.design.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun KAlertDialog(
    show: Boolean,
    title: String,
    text: String,
    onDismiss: () -> Unit
) {
    if (show) {
        AlertDialog(
            onDismissRequest = onDismiss,
            confirmButton = {
                Button(onClick = onDismiss) {
                    Text(text = "OK")
                }
            },
            title = { Text(text = title) },
            text = { Text(text = text) }
        )
    }
}