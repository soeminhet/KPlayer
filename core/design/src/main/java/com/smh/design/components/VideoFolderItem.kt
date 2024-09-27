package com.smh.design.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.smh.design.R
import com.smh.design.extension.sOrEs
import com.smh.design.theme.KPlayerTheme

@Composable
fun VideoFolderItem(
    modifier: Modifier = Modifier,
    folderName: String,
    videoCount: Int,
    onClick: (String) -> Unit
) {
    Row(
        modifier = modifier
            .clickable { onClick(folderName) }
            .padding(horizontal = 16.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Image(
            imageVector = ImageVector.vectorResource(id = R.drawable.img_folder),
            contentDescription = "Folder",
            modifier = Modifier.size(80.dp)
        )

        Column {
            Text(
                text = folderName,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "$videoCount ${videoCount.sOrEs("video")}",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Preview
@Composable
private fun VideoFolderItemPreview() {
    KPlayerTheme {
        VideoFolderItem(
            folderName = "Folder Name",
            videoCount = 5,
            onClick = {}
        )
    }
}